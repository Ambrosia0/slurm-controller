package com.ambrosia.cluster_controller.taskSchedulers.slurm;

import com.ambrosia.cluster_controller.model.DTO.scheduler.ProfileTask;
import com.ambrosia.cluster_controller.model.entity.Cluster;

/**
 * Service for managing tokens for api interaction with slurm
 * SlurmTokenManager
 */
public interface SlurmTokenManager {
    /**
     * Obtains token using admin user data in Cluster
     * @param cluster
     * @return auth token
     */
    String getToken(Cluster cluster);
    
    /**
     * Obtains token using admin user data in cluster, ignores already present tokens
     * @param cluster
     * @return auth token
     */
    String getTokenForced(Cluster cluster);
    /**
     * Obtains token using user profile information
     * @param cluster Cluster against which token is requested
     * @param profile User information
     * @return
     */
    String getUserToken(Cluster cluster, ProfileTask profile);
    
    /**
     * Obtains token using user profile, ignores already present tokens
     * @param cluster
     * @param profile
     * @return
     */
    String getUserTokenForced(Cluster cluster, ProfileTask profile);
}
