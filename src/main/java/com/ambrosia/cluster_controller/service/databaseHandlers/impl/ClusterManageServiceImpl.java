package com.ambrosia.cluster_controller.service.databaseHandlers.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.cluster_controller.config.ClientSessionManager;
import com.ambrosia.cluster_controller.exception.api.ClusterDoesntExistException;
import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.ClusterAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.ClusterFilter;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.repository.specification.ClusterSpecification;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterManageService;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileRemover;
import com.ambrosia.cluster_controller.service.mappers.ClusterMapper;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SchedulerVerificator;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmClusterRec;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ClusterManageServiceImpl implements ClusterManageService {
    
    private final ClusterRepository clusterRepository;

    private final ClusterMapper clusterMapper;

    private final ClientSessionManager clientSessionManager;

    private final SchedulerVerificator schedulerVerificator;

    private final SlurmBindedClusterManager slurmBindedClusterManager;

    private final ClusterProfileRemover clusterProfileRemover;

    @Override
    @Transactional
    public ClusterAdminResponse addCluster(ClusterAdminRequest dto) {
        var cluster = clusterMapper.toEntity(dto);
        if (clusterRepository.existsByHost(cluster.getHost()) || 
            !schedulerVerificator.isSchedulerCorrect(
                cluster.getHost(), 
                cluster.getUsername(), 
                cluster.getPassword(), 
                cluster.getSshPort()
            )
        ) {
            throw new ClusterDoesntExistException();
        };
        return clusterMapper.toAdminResponse(clusterRepository.saveAndFlush(cluster));
    }

    @Transactional
    @Override
    public void deleteCluster(long id) {
        var cluster = clusterRepository.findById(id)
            .orElseThrow(() -> new ClusterDoesntExistException());
        clientSessionManager.closeConnection(cluster.getUsername(), cluster.getHost());

        clusterProfileRemover.removeProfilesByClusterId(id);
        clusterRepository.deleteById(id);
    }

    @Override
    public Page<ClusterAdminResponse> getClusters(ClusterFilter clusterFilter, Pageable pageable) {
        var spec = Specification.<Cluster>unrestricted()
            .and(ClusterSpecification.displayedNameContains(clusterFilter.displayedName()));
        return clusterRepository.findAll(spec, pageable)
            .map(clusterMapper::toAdminResponse);
    }

    @Override
    public List<SlurmClusterRec> getBindedClusters(long clusterId) {
        var cluster = clusterRepository.findById(clusterId)
            .orElseThrow(() -> new ClusterDoesntExistException());
        return slurmBindedClusterManager.getBindedClusters(cluster.getId());
    }
}
