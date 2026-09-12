package com.ambrosia.cluster_controller.taskSchedulers.slurm;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmClusterRec;

import jakarta.annotation.Nullable;
public interface SlurmBindedClusterManager {
    List<SlurmClusterRec> getBindedClusters(long clusterId);
    @Nullable SlurmClusterRec getBinded(long clusterId, String bindedName);
    void updateBindedClusters();
    void deleteBinded();
}
