package com.ambrosia.cluster_controller.taskSchedulers.slurm.impl;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ambrosia.cluster_controller.model.DTO.scheduler.ProfileTask;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTokenManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTokenRequester;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmUserTokenData;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlurmTokenManagerImpl implements SlurmTokenManager{
    
    private final ClusterRepository clusterRepository;

    private Map<Long, SlurmUserTokenData> adminTokens = new ConcurrentHashMap<>();

    private Map<Long, SlurmUserTokenData> userTokens = new ConcurrentHashMap<>();

    private final SlurmTokenRequester slurmTokenRequester;

    public @Nullable String getToken(Cluster cluster){
        if(!adminTokens.containsKey(cluster.getId())){
            return requestToken(cluster);

        }else{
            if(Instant.now().plusSeconds(30).isAfter(adminTokens.get(cluster.getId()).expirationTime())){
                return requestToken(cluster);
            }else{
                return adminTokens.get(cluster.getId()).token();
            }
        }
    }

    @Override
    public String getTokenForced(Cluster cluster) {
        return requestToken(cluster);
    }

    // updates token hashMap if cluster is deleted
    @Scheduled(fixedDelay = 60000)
    public void updateTokens(){
        var clusters = clusterRepository.findClustersByTaskSchedulerString("SLURM")
            .stream()
            .collect(Collectors.groupingBy(t -> t.getId()));
        adminTokens.entrySet().removeIf(entry -> !clusters.containsKey(entry.getKey()));
    }

    public String getUserToken(Cluster cluster, ProfileTask profile){
        if (!userTokens.containsKey(profile.userId())) {
            return requestUserToken(cluster, profile);
        } else {
            if (Instant.now().plusSeconds(30).isAfter(
                    userTokens.get(profile.userId()).expirationTime())) {
                return requestUserToken(cluster, profile);
            } else {
                return userTokens.get(profile.userId()).token();
            }
        }
    }

    @Override
    public String getUserTokenForced(Cluster cluster, ProfileTask profile) {
        return requestUserToken(cluster, profile);
    }

    private String requestToken(Cluster cluster){
        var tokenData = slurmTokenRequester.refreshToken(cluster);
        adminTokens.put(cluster.getId(), tokenData);
        return tokenData.token();
    }

    private String requestUserToken(Cluster cluster, ProfileTask profile){
        var tokenData = slurmTokenRequester.requestUserToken(cluster, profile);
        userTokens.put(profile.userId(), tokenData);
        return tokenData.token();
    }

    @Scheduled(fixedRate = 1800000)
    public void removeExpiredUserTokens(){
        userTokens.values().removeIf(val -> Instant.now().isAfter(val.expirationTime()));
    }
}
