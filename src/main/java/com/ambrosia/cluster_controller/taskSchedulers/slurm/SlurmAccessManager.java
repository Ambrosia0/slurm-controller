package com.ambrosia.cluster_controller.taskSchedulers.slurm;

import java.util.List;

import com.ambrosia.cluster_controller.model.DTO.scheduler.Association;
import com.ambrosia.cluster_controller.model.DTO.scheduler.Profile;
import com.ambrosia.cluster_controller.model.entity.Cluster;
/**
 * Managing access and resources on bound slurm clusters through rest api (slurmrestd)
 * 
 * SlurmAccessManager
 */
public interface SlurmAccessManager {
    /**
     * Creates user profiles on slurmdbd node
     * @param profile
     * @return
     */
    void createProfile(Cluster cluster, List<Profile> toCreate);

    /**
     * Creates associations with clusters
     * @param cluster
     * @param boundCluster
     * @param profiles
     */
    void provideAccessToBindedCluster(Cluster cluster, String boundCluster, List<Association> associations);

    /**
     * Deletes associations from slurmdbd node
     * @param profile
     */
    void revokeAccessToBindedCluster(Cluster cluster, String boundCluster, List<String> usernames);

    /**
     * Sets external limits (quota)
     * @param profile
     * @return
     */
    void setProfileLimits(Cluster cluster, String clusterName, List<Association> profileCreate);

    /**
     * Deletes users from slurmdbd node
     * @param cluster
     * @param username
     */
    void deleteProfile(Cluster cluster, List<String> usernames);
}
