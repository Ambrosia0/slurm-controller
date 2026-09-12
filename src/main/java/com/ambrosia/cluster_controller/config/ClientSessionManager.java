package com.ambrosia.cluster_controller.config;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Component for ClientSession creation
 */
@Slf4j
@RequiredArgsConstructor 
@Component
public class ClientSessionManager {
    private final SshClient sshClient;

    private Map<String, ClientSession> connectionMap = new ConcurrentHashMap<>();

    public ClientSession getConnection(String host, String username, String password, int port) throws IOException{
        if(connectionMap.containsKey(host+":"+username) && connectionMap.get(host+":"+username).isOpen()){
            return connectionMap.get(host+":"+username);
        } else{
            try {
                ClientSession session = sshClient.connect(username, host, port)
                    .verify()
                    .getSession();
                if(password != null){
                    session.addPasswordIdentity(password);
                }
                session.auth().verify();
                connectionMap.put(host+":"+username, session);
                return session;
            } catch (IOException e) {
                log.warn(
                    String.format("Cannot connect to host: %s with username: %s",
                        host, 
                        username
                    )
                );
                throw e;
            }
        }
    }

    public boolean closeConnection(String username, String host){
        try {
            var key = host+":"+username;
            if(!connectionMap.isEmpty()){
                connectionMap.get(key).close();
                connectionMap.remove(key);
            }
            return true;
        } catch (IOException e) {
            log.error("Error while trying to use closed connection", e);
            return true;
        }
    }
}
