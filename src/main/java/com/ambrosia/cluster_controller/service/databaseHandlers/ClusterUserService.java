package com.ambrosia.cluster_controller.service.databaseHandlers;

import java.util.List;

import com.ambrosia.cluster_controller.model.DTO.user.response.BindedClusterResponse;
import com.ambrosia.cluster_controller.model.DTO.user.response.ClusterUserResponse;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;

/**
 * Service for providing information about profiles on clusters for users
 */
public interface ClusterUserService {
    /**
     * @param userId Requesting user ID
     * @return Available slurmdbd nodes (those where profile is exists)
     */
    List<ClusterUserResponse> getAvailableClusters(long userId);

    /**
     * Get bound to slurmdbd node clusters on which user profile is exists
     * @param clusterId 
     * @param userId
     * @return Clusters, bound to the slurmdbd node with user profile
     */
    List<BindedClusterResponse> getBindedClusters(long clusterId, long userId);

    /**
     * Gets TRES on slurmdbd node
     * @param clusterId Slurmdbd node id
     * @return Available TRES
     */
    List<SlurmTres> getTres(long clusterId);
}
