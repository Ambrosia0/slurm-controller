package com.ambrosia.cluster_controller.controller.api.ws;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.ambrosia.cluster_controller.config.security.CustomUserDetails;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.taskSchedulers.SchedulerHandler;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmClusterRec;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StatisticsHandler extends TextWebSocketHandler{
    private final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    private final SchedulerHandler slurmAdminSchedulerHandler;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var userDetails = (CustomUserDetails)session.getAttributes().get("user");
        if(userDetails.getAuthorities().stream().anyMatch(authority -> "ROLE_USER".equals(authority.getAuthority()))){
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        var existingSession = userSessions.get(userDetails.getUsername());
        if(existingSession != null){
            userSessions.remove(userDetails.getUsername());
            existingSession.close(CloseStatus.POLICY_VIOLATION);
        }
        userSessions.put(userDetails.getUsername(), session);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        var userDetails = (CustomUserDetails)session.getAttributes().get("user");
        userSessions.remove(userDetails.getUsername());
    }

    @Scheduled(fixedDelay = 5000)
    private void sendStatistics(){
        if(userSessions.isEmpty()){
            return;
        }
        var clusters = userSessions.entrySet().stream()
            .map(val -> (Cluster)val.getValue().getAttributes().get("cluster"))
            .distinct()
            .collect(Collectors.toSet());
        clusters.forEach(cluster ->{
            getBindedClusters(cluster).forEach( bindedCluster ->{
                var statistics = slurmAdminSchedulerHandler.getStatistics(cluster, bindedCluster);
                userSessions.values().stream()
                .filter(val -> (((Cluster)val.getAttributes().get("cluster")).getId() == cluster.getId() 
                        && ((SlurmClusterRec)val.getAttributes().get("bindedCluster")).name().equals(bindedCluster)))
                .forEach(val -> {
                    try {
                        if(statistics != null){
                            val.sendMessage(new TextMessage(objectMapper.writeValueAsString(statistics)));
                         }
                         // else{
                        //     val.getValue().close(CloseStatus.BAD_DATA);
                        // }
                    } catch (Exception e) {
                        log.error("Can't send statistics message", e);
                    }
                });  
            });  
        });
    }

    private Set<String> getBindedClusters(Cluster targetCluster){
        return userSessions.values().stream()
            .filter(session ->{
                Object clusterObj = session.getAttributes().get("cluster");
                return clusterObj instanceof Cluster && clusterObj.equals(targetCluster);
            })
            .map(session -> ((SlurmClusterRec)session.getAttributes().get("bindedCluster")).name())
            .distinct()
            .collect(Collectors.toSet());
    }
}
