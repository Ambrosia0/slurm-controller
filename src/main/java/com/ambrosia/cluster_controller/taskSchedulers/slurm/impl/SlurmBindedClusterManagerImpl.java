package com.ambrosia.cluster_controller.taskSchedulers.slurm.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTokenManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmClusterRec;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmController;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmClusterResponse;
import com.ambrosia.cluster_controller.util.SlurmRestUtil;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SlurmBindedClusterManagerImpl implements SlurmBindedClusterManager {
    
    private final SlurmTokenManager tokenManager;

    private final ClusterRepository clusterRepository;

    private final RestClient restClient;

    private Map<Long, List<SlurmClusterRec>> bindedClustersMap = new ConcurrentHashMap<>();


    public List<SlurmClusterRec> getBindedClusters(long clusterId){
        return bindedClustersMap.getOrDefault(clusterId, List.of());
    }

    public @Nullable SlurmClusterRec getBinded(long clusterId, String bindedName){
        var result = bindedClustersMap.getOrDefault(clusterId, null);
        if(result != null){
            for(SlurmClusterRec rec: result){
                if(rec.name().equals(bindedName))
                    return rec;
            }
            return null;
        }else{
            return null;
        }
    }

    @PostConstruct
    public void init(){
        updateBindedClusters();
    }

    @Transactional(readOnly = true)
    @Scheduled(fixedDelay = 20000)
    public void updateBindedClusters(){
        try (var stream = clusterRepository.streamAll()) {
            stream.forEach(cluster ->{
                var token = tokenManager.getToken(cluster);
                if (token == null) {
                    log.warn("Cluster " + cluster.getDisplayedName() + " can't be accessed");
                    return; 
                }
                var bindedClusters = restClient.get()
                        .uri(
                            SlurmRestUtil.DATABASE_URL+"/clusters/", 
                            cluster.getSchema().getName(),
                            cluster.getHost(),
                            cluster.getDaemonPort(),
                            cluster.getScheduler().getVersion()
                        )
                        .header("X-SLURM-USER-NAME", cluster.getUsername())
                        .header("X-SLURM-USER-TOKEN", token)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (request, response) -> {
                            log.error(
                                "Error while getting binded clusters on {}! {}",
                                cluster.getHost()+":"+cluster.getDaemonPort(), 
                                new String(response.getBody().readAllBytes())
                            );
                        })
                        .body(SlurmClusterResponse.class);
                if(bindedClusters == null){
                    log.warn("Binded clusters not found for cluster {}!", cluster.getHost());
                    return;
                }
                bindedClustersMap.put(
                    cluster.getId(), 
                    bindedClusters.clusters()
                        .stream()
                        .map(t -> {
                            // allows nodes placed on slurmdbd node
                            if(t.controller().host().contains("127.0.0.1"))
                                return SlurmClusterRec.builder()
                                    .controller(SlurmController.builder()
                                        .host(cluster.getHost())
                                        .port(t.controller().port())
                                        .build()
                                    )
                                    .name(t.name())
                                    .associations(t.associations())
                                    .slurmVersion(t.slurmVersion())
                                    .tres(t.tres())
                                    .build();
                            return t;
                        })
                        .toList()
                        
                );
            });
        } catch (Exception e) {}
    }

    @Transactional(readOnly = true)
    @Scheduled(fixedDelay = 20000)
    public void deleteBinded(){
        try (var stream = clusterRepository.streamAllIds()) {
            var clSet = stream.collect(Collectors.toSet());
            bindedClustersMap.entrySet().removeIf(entry -> !clSet.contains(entry.getKey()));
        } catch (Exception e) {}
    }
}
