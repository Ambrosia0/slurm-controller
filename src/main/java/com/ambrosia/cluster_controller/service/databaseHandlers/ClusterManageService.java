package com.ambrosia.cluster_controller.service.databaseHandlers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.ClusterAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.ClusterFilter;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmClusterRec;

/**
 * Service for managing slurmdbd connected nodes
 */
public interface ClusterManageService {
    /**
     * Creates node entity in database
     * @param requst DTO with node information
     * @return Created entity
     */
    ClusterAdminResponse addCluster(ClusterAdminRequest request);
    // public boolean updateCluster(ClusterAdminRequest clusterDto);

    /**
     * Deleted node entity from database
     * @param id
     */
    void deleteCluster(long id);

    /**
     * Pagination through entities
     * @param pageable
     * @return 
     */
    Page<ClusterAdminResponse> getClusters(ClusterFilter clusterFilter, Pageable pageable);
    
    /**
     * Gets bound to slurmdbd node clusters
     * @param clusterId
     * @return
     */
    List<SlurmClusterRec> getBindedClusters(long clusterId);
}
