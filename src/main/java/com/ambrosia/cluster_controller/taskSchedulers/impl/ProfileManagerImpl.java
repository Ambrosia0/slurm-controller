package com.ambrosia.cluster_controller.taskSchedulers.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ambrosia.cluster_controller.model.DTO.scheduler.Association;
import com.ambrosia.cluster_controller.model.DTO.scheduler.Profile;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.service.systemServices.SystemProfileService;
import com.ambrosia.cluster_controller.taskSchedulers.ProfileManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmAccessManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j 
@RequiredArgsConstructor 
@Service 
public class ProfileManagerImpl implements ProfileManager{
    private final SlurmAccessManager slurmAccessManager;

    private final SystemProfileService systemProfileService;

    @Override
    public List<Integer> provideClusterAccess(Cluster cluster, String boundCluster, List<Association> associations) {
        Assert.notNull(associations, "associations must not be null!");
        Assert.notNull(cluster, "Cluster must not be null!");
        Assert.notNull(boundCluster, "Clsuser name must not be null!");

        if(associations.isEmpty())
            return List.of();

        var ids = systemProfileService.createProfile(cluster, boundCluster, associations);
        try {
            slurmAccessManager.provideAccessToBindedCluster(cluster, boundCluster, associations);
            return ids;
        } catch (Exception e) {
            log.error("Can't create profiles! {}", e);
            systemProfileService.deleteProfile(
                cluster, 
                boundCluster, 
                associations.stream()
                    .map(t -> t.username().toLowerCase())
                    .toList()
            );
            throw e;
        }
    }

    @Override
    public void createProfile(Cluster cluster, List<Profile> profiles) {
        Assert.notNull(profiles, "profiles must not be null!");
        Assert.notNull(cluster, "Cluster must not be null!");

        if(profiles.isEmpty())
            return;

        slurmAccessManager.createProfile(cluster, profiles);
    }

    @Override
    public void revokeClusterAccess(Cluster cluster, String boundCluster, List<Association> profileDtos) {
        Assert.notNull(profileDtos, "Profiles must not be null!");
        Assert.notNull(cluster, "Cluster must not be null!");
        Assert.notNull(boundCluster, "Cluser name must not be null!");

        if(profileDtos.isEmpty())
            return;

        var usernames = profileDtos.stream().map(t -> t.username()).toList();
        slurmAccessManager.revokeAccessToBindedCluster(cluster, boundCluster, usernames);
        try {
            systemProfileService.deleteProfile(cluster, boundCluster, usernames);
        } catch (Exception e) {
            log.error("Can't revoke access to cluster! {}", e);
            slurmAccessManager.provideAccessToBindedCluster(cluster, boundCluster, profileDtos);
            throw e;
        }
    }
    
    @Override
    public void deleteProfile(Cluster cluster, List<String> usernames){
        Assert.notNull(cluster, "Cluster must not be null!");

        if(usernames.isEmpty())
            return;

        slurmAccessManager.deleteProfile(cluster, usernames);
    }
    
    @Override
    public void updateProfile(Cluster cluster, String boundCluster, List<Association> profileDtos) {
        Assert.notNull(cluster, "Cluster must not be null!");
        Assert.notNull(profileDtos, "Profiles must not be null!");

        if(profileDtos.isEmpty())
            return;
        
        slurmAccessManager.setProfileLimits(cluster, boundCluster, profileDtos);
        systemProfileService.updateLimits(cluster, boundCluster, profileDtos);
    }
}
