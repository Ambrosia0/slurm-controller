package com.ambrosia.cluster_controller.taskSchedulers.policy;

import com.ambrosia.cluster_controller.exception.api.ClusterUnavailableException;
import com.ambrosia.cluster_controller.model.DTO.scheduler.ProfileTask;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTokenManager;

public class UserActor extends AbstractPolicy{
    private final ProfileTask profileTask;

    public UserActor(ProfileTask profileTask, Cluster cluster){
        super(cluster);
        this.profileTask = profileTask;
    }

    @Override
    public String getToken(SlurmTokenManager slurmTokenManager) {
        var token = slurmTokenManager.getUserToken(cluster, profileTask);
        if(token == null){
            throw new ClusterUnavailableException();
        }
        return token;
    }

    @Override
    public String getTokenForced(SlurmTokenManager slurmTokenManager) {
        return slurmTokenManager.getUserTokenForced(cluster, profileTask);
    }

    @Override
    public String getUsername() {
        return profileTask.username();
    }

    @Override
    public boolean isGlobalViewAllowed() {
        return false;
    }

    public static UserActor create(Cluster cluster, ProfileTask profileTask){
        return new UserActor(profileTask, cluster);
    }
}
