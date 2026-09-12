package com.ambrosia.cluster_controller.service.systemServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.config.ClientSessionManager;
import com.ambrosia.cluster_controller.config.security.PasswordEncryptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Класс для выполнения команд на кластерах через ssh-протокол
@Slf4j
@RequiredArgsConstructor
@Component
public class SshCommandSender {
    private final ClientSessionManager clientSessionManager;

    private final PasswordEncryptor passwordEncryptor;

    public String execute(String host, String username, String password, int port, String command, boolean withResult){
        try{
            var connection = clientSessionManager.getConnection(
                host, 
                username, 
                password == null? null: passwordEncryptor.decode(password), 
                port
            );
            var responseStream = new ByteArrayOutputStream();
            try (var execChannel = connection.createExecChannel(command)) {
                execChannel.setUsePty(true);
                execChannel.setErr(responseStream);
                if(withResult){
                    execChannel.setOut(responseStream);
                }
                execChannel.open().verify(5, TimeUnit.SECONDS);
                execChannel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), Duration.ofSeconds(5));
            }
            var result = responseStream.toString(StandardCharsets.UTF_8);
            responseStream.close();
            return result;
        } catch (IOException ex) {
            log.warn(
                "Can't execute SSH command on cluster {} with username={} message={}", 
                host, 
                username, 
                ex.getMessage()
            );
            throw new RuntimeException("Can't execute!");
        }
    }
    

    public String executeOnce(String host, String username, String password, int port, String command){
        try(
            var connection = clientSessionManager.getConnection(
                    host,
                    username,
                    password == null? null: passwordEncryptor.decode(password),
                    port) ) {
            var responseStream = new ByteArrayOutputStream();
            try (var execChannel = connection.createExecChannel(command)) {
                execChannel.setUsePty(true);
                execChannel.setErr(responseStream);
                execChannel.setOut(responseStream);
                execChannel.open().verify(5, TimeUnit.SECONDS);
                execChannel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), Duration.ofSeconds(5));
            }
            var result = responseStream.toString(StandardCharsets.UTF_8);
            responseStream.close();
            return result;
        } catch (IOException ex) {
            log.error(
                "Can't executeOnce SSH command on cluster {} message={}",
                host,
                ex.getMessage()
            );
            throw new RuntimeException("Can't execute!");
        }
    }

    public ChannelShell initShell(String host, String username, String password, int port){
        try {
            var clientSession = clientSessionManager.getConnection(
                host, 
                username, 
                password, 
                port
            );
            var channelShell = clientSession.createShellChannel();
            channelShell.setUsePty(true);
            channelShell.setPtyType("xterm");
            channelShell.setPtyColumns(100);
            channelShell.setPtyLines(80);
            return channelShell;
        } catch (IOException e) {
            log.error(
                "Can't init shell on cluster {} message=", host, e.getMessage());
                throw new RuntimeException("Can't initiate shell!");
        }
    }

    public void closeChannelShell(ChannelShell channelShell){
        if (channelShell != null) {
            var address = (InetSocketAddress) channelShell.getClientSession().getConnectAddress();
            if(channelShell.isOpen())
                channelShell.close(true);
            clientSessionManager.closeConnection(
                    channelShell.getClientSession().getUsername(),
                    address.getHostString());
        }
    }
}
