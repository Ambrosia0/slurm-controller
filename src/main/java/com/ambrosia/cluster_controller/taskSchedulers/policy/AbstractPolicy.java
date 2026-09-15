package com.ambrosia.cluster_controller.taskSchedulers.policy;

import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTokenManager;

public abstract class AbstractPolicy implements TaskGetPolicy{
    protected final Cluster cluster;

    protected AbstractPolicy(Cluster cluster){
        this.cluster = cluster;
    }

    public abstract String getUsername();
    public abstract String getToken(SlurmTokenManager slurmTokenManager);
    public abstract String getTokenForced(SlurmTokenManager slurmTokenManager);
}
