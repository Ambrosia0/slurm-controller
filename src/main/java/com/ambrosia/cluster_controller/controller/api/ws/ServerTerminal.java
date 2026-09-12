package com.ambrosia.cluster_controller.controller.api.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.ambrosia.cluster_controller.config.security.CustomUserDetails;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;

@Component
public class ServerTerminal extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final boolean isWindows = System.getProperty("os.name").contains("Windows");
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var userDetails = (CustomUserDetails) session.getAttributes().get("user");
        
        if(userDetails.getAuthorities().stream().anyMatch(authority -> "USER".equals(authority.getAuthority()))){
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        if(sessions.contains(Long.toString(userDetails.getId()))){
            var existedSession = sessions.get(userDetails.getUsername());
            existedSession.close(CloseStatus.POLICY_VIOLATION);
            sessions.remove(userDetails.getUsername());
        }

        Map<String, String> env = new HashMap<>(System.getenv());
        env.put("PATH", System.getenv("PATH"));
        env.put("TERM", "xterm-256color");
        env.put("COLORTERM", "truecolor");

        PtyProcess ptyProcess = new PtyProcessBuilder()
            .setCommand(isWindows ? 
                new String[]{"pwsh.exe"} : 
                new String[]{"/bin/sh", "--login"})
            .setEnvironment(env)
            .setConsole(false)
            .start();

        session.sendMessage(new TextMessage("PTY PID: " + ptyProcess.pid() + "\r\n$"));

        executorService.submit(() -> handleProcessOutput(ptyProcess.getInputStream(), session, false));
        executorService.submit(() -> handleProcessOutput(ptyProcess.getErrorStream(), session, true));

        session.getAttributes().put("process", ptyProcess);
        sessions.put(session.getId(), session);
    }

    private void handleProcessOutput(InputStream input, WebSocketSession session, boolean isError) {
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

            PtyProcess process = (PtyProcess) session.getAttributes().get("process");
            if (process != null) {
                process.setWinSize(new WinSize(cols, rows));
            }
            return;
        }

        PtyProcess process = (PtyProcess) session.getAttributes().get("process");
        if (process != null && process.isAlive()) {
            OutputStream out = process.getOutputStream();
            out.write(payload.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        PtyProcess process = (PtyProcess) session.getAttributes().get("process");
        if (process != null) {
            process.destroyForcibly();
        }
        sessions.remove(session.getId());
    }
}
