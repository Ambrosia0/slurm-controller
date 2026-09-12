package com.ambrosia.cluster_controller.taskSchedulers.slurm;

import com.ambrosia.cluster_controller.model.DTO.scheduler.ProfileTask;
import com.ambrosia.cluster_controller.model.entity.Cluster;

public interface SlurmTokenRequester {
    SlurmUserTokenData refreshToken(Cluster cluster);
    SlurmUserTokenData requestUserToken(Cluster cluster, ProfileTask profile);
}
