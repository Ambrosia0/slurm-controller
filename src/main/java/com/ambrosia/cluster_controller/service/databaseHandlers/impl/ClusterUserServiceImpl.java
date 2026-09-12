package com.ambrosia.cluster_controller.service.databaseHandlers.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ambrosia.cluster_controller.exception.api.AccessDoesntProvidedException;
import com.ambrosia.cluster_controller.exception.api.ClusterDoesntExistException;
import com.ambrosia.cluster_controller.model.DTO.user.response.BindedClusterResponse;
import com.ambrosia.cluster_controller.model.DTO.user.response.ClusterUserResponse;
import com.ambrosia.cluster_controller.repository.ClusterProfileRepository;
import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterUserService;
import com.ambrosia.cluster_controller.service.mappers.ClusterMapper;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTresRequester;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class ClusterUserServiceImpl implements ClusterUserService{
    private final ClusterRepository clusterRepository;

    private final ClusterMapper clusterMapper;

    private final ClusterProfileRepository profileRepository;

    private final SlurmBindedClusterManager slurmBindedClusterManager;
    
    private final SlurmTresRequester slurmTresRequester;

    @Override
    public List<ClusterUserResponse> getAvailableClusters(long userId) {
        return clusterRepository.findByUserId(userId)
            .stream().map(clusterMapper::toUserResponse).collect(Collectors.toList());
    }

    @Override
    public List<BindedClusterResponse> getBindedClusters(long clusterId, long userId) {
        var bindedClusters = profileRepository.findBindedClustersForUser(clusterId, userId);
        if(bindedClusters.isEmpty()){
            throw new AccessDoesntProvidedException();
        }
        var currentBinded = slurmBindedClusterManager.getBindedClusters(clusterId);
        if(currentBinded == null){
            return List.of();
        }
        return currentBinded.stream()
            .map(val -> new BindedClusterResponse(val.name(), val.tres(), val.nodes()))
            .filter(val -> bindedClusters.contains(val.name()))
            .toList();
    }

    @Override
    public List<SlurmTres> getTres(long clusterId) {
        var tres = slurmTresRequester.getSupportedTres(clusterId);
        if(tres.isEmpty())
            throw new ClusterDoesntExistException();
        return tres;
    }
}
