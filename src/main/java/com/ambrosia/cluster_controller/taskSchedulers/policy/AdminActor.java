package com.ambrosia.cluster_controller.taskSchedulers.policy;

import com.ambrosia.cluster_controller.exception.api.ClusterUnavailableException;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTokenManager;

public class AdminActor extends AbstractPolicy{
    public AdminActor(Cluster cluster){
        super(cluster);
    }

    @Override
    public String getToken(SlurmTokenManager slurmTokenManager) {
        var token = slurmTokenManager.getToken(cluster);
        if(token == null){
            throw new ClusterUnavailableException();
        }
        return token;
    }

    @Override
    public String getTokenForced(SlurmTokenManager slurmTokenManager) {
        return slurmTokenManager.getTokenForced(cluster);
    }

    @Override
    public String getUsername() {
        return cluster.getUsername().toLowerCase();
    }

    @Override
    public boolean isGlobalViewAllowed() {
        return true;
    }

    public static AdminActor create(Cluster cluster){
        return new AdminActor(cluster);
    }
}
