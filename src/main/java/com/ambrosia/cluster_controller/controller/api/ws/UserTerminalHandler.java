package com.ambrosia.cluster_controller.controller.api.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.sshd.client.channel.ChannelShell;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.ambrosia.cluster_controller.config.security.CustomUserDetails;
import com.ambrosia.cluster_controller.config.security.PasswordEncryptor;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.repository.ClusterProfileRepository;
import com.ambrosia.cluster_controller.service.systemServices.SshCommandSender;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmClusterRec;

import lombok.RequiredArgsConstructor;

/**
 * WS-endpoint for interaction with bound clusters through ssh with user profile credentials
 * UserTerminalHandler
 */
@RequiredArgsConstructor
@Component
public class UserTerminalHandler extends TextWebSocketHandler{
    private final ClusterProfileRepository clusterProfileRepository;

    private final SshCommandSender sshCommandSender;

    private final PasswordEncryptor passwordEncryptor;

    private final ConcurrentHashMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var cluster = (Cluster)session.getAttributes().get("cluster");
        var userDetails = (CustomUserDetails)session.getAttributes().get("user");
        var bindedCluster = (SlurmClusterRec)session.getAttributes().get("bindedCluster");
        

        var profileOpt = clusterProfileRepository.findByIdsWS(
            cluster.getId(), 
            userDetails.getId(), 
            bindedCluster.name());
        if(!profileOpt.isPresent()){
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        var profile = profileOpt.get();

        if(userSessions.contains(userDetails.getId())){
            var existedSession = userSessions.get(userDetails.getId());
            sshCommandSender.closeChannelShell(
                ((ChannelShell)existedSession.getAttributes().get("channel-shell"))
            );
            existedSession.close(CloseStatus.POLICY_VIOLATION);
            userSessions.remove(userDetails.getId());
        }

        var channelShell = sshCommandSender.initShell(
            bindedCluster.controller().host(),
            profile.getId().getUser().getUsername().toLowerCase(),
            passwordEncryptor.decode(profile.getPassword()),
            cluster.getSshPort()
        );
        channelShell.open().verify(Duration.ofSeconds(5));

        executorService.submit(() -> handleOutput(channelShell.getInvertedOut(), session, false));
        executorService.submit(() -> handleOutput(channelShell.getInvertedErr(), session, true));

        session.getAttributes().put("channel-shell", channelShell);
        userSessions.put(userDetails.getId(), session);
    }
    
    private void handleOutput(InputStream input, WebSocketSession session, boolean isError) {
        byte[] buffer = new byte[8192];
        int bytesRead;
        try {
            while ((bytesRead = input.read(buffer)) != -1 && session.isOpen()) {
                String output = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                if (isError) {
                    output = "\u001B[31m" + output + "\u001B[0m";
                }
                session.sendMessage(new TextMessage(output));
            }
        } catch (IOException e) {
            if (session.isOpen()) {
                try {
                    session.close(CloseStatus.SERVER_ERROR);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var payload = message.getPayload();
        if (payload.startsWith("{\"type\":\"resize\"")) {
            String[] parts = payload.split("\"cols\":|,\"rows\":|}");
            int cols = Integer.parseInt(parts[1].trim());
            int rows = Integer.parseInt(parts[2].trim());
            ChannelShell shell = (ChannelShell) session.getAttributes().get("channel-shell");
            if (shell != null) {
                shell.setPtyColumns(cols);
                shell.setPtyLines(rows);
            }
            return;
        }

        ChannelShell shell = (ChannelShell) session.getAttributes().get("channel-shell");
        if (shell != null && shell.isOpen()) {
            OutputStream out = shell.getInvertedIn();
            out.write(payload.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
    }


    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        var shell = (ChannelShell)session.getAttributes().get("channel-shell");
        sshCommandSender.closeChannelShell(shell);
        userSessions.remove(((CustomUserDetails)session.getAttributes().get("user")).getId());
    }
}
