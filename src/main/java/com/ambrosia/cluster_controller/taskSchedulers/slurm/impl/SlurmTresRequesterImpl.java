package com.ambrosia.cluster_controller.taskSchedulers.slurm.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.taskSchedulers.SchedulerHandler;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTresRequester;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class SlurmTresRequesterImpl implements SlurmTresRequester{
    private Map<Long, List<SlurmTres>> tresMap = new ConcurrentHashMap<>(16, 0.8f);

    private final ClusterRepository clusterRepository;

    private final SchedulerHandler schedulerHandler;

    @Override
    public List<SlurmTres> getSupportedTres(Long clusterId) {
        return tresMap.getOrDefault(clusterId, List.of());
    }

    @Scheduled(fixedRate = 20000)
    public void tresPoll(){
        clusterRepository.findClustersByTaskSchedulerString("SLURM")
            .forEach(cluster -> {
                tresMap.put(cluster.getId(), schedulerHandler.getTres(cluster));
            });
    }
}
