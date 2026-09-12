package com.ambrosia.cluster_controller.taskSchedulers;

import java.util.List;

import com.ambrosia.cluster_controller.model.DTO.scheduler.Association;
import com.ambrosia.cluster_controller.model.DTO.scheduler.Profile;
import com.ambrosia.cluster_controller.model.entity.Cluster;

/**
 * Creates profiles on OS and slurmdbd nodes. Compensates failed oprations on system profiles and slurm profiles
 * ProfileManager
 */
public interface ProfileManager {
    /**
     * Batch creation of profiles on system & associations 
     * @param clusterId
     * @param boundCluster
     * @param clusterProfile
     * @return Ids of created users
     */
    List<Integer> provideClusterAccess(Cluster cluster, String boundCluster, List<Association> associations);
    
    /**
     * Creation of profiles on slurmdbd
     * @param cluster
     * @param boundCluster
     * @param usernames
     */
    void createProfile(Cluster cluster, List<Profile> profiles);

    /**
     * Batch profile update
     * @param clusterProfile
     */
    void updateProfile(Cluster cluster, String boundCluster, List<Association> associations);
    
    /**
     * Deletes associations with bound cluster
     * @param clusterId slurmdbd node id
     * @param boundCluster bound cluster name
     * @param username the username whose association is being deleted
     */
    void revokeClusterAccess(Cluster cluster, String boundCluster, List<Association> associations);

    /**
     * Deletes user from slurmdbd node
     * @param clusterId slurmdbd node id
     * @param usernames the usernames whose record is being deleted
     */
    void deleteProfile(Cluster cluster, List<String> usernames);
}
