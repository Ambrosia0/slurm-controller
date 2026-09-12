package com.ambrosia.cluster_controller.taskSchedulers.slurm;

import com.ambrosia.cluster_controller.model.entity.Cluster;

public interface SlurmProfileChecker {
    boolean isProfileExist(Cluster cluster, String boundCluster, String username);
}
