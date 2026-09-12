package com.ambrosia.cluster_controller.service.systemServices.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ambrosia.cluster_controller.model.DTO.scheduler.Association;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.service.systemServices.SshCommandSender;
import com.ambrosia.cluster_controller.service.systemServices.SystemProfileChecker;
import com.ambrosia.cluster_controller.service.systemServices.SystemProfileService;
import com.ambrosia.cluster_controller.service.systemServices.script.SystemScriptBuilder;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SystemProfileServiceImpl implements SystemProfileService, SystemProfileChecker {
    private final SshCommandSender sshCommandSender;

    private final SlurmBindedClusterManager bindedClusterManager;

    private final SystemScriptBuilder systemScriptBuilder;

    @Override
    public List<Integer> createProfile(Cluster cluster, String clusterName, List<Association> clusterProfile) {
        var bindedCluster = bindedClusterManager.getBinded(
            cluster.getId(),
            clusterName
        );

        var result = sshCommandSender.execute(
            bindedCluster.controller().host(),
            cluster.getUsername(),
            cluster.getPassword(),
            cluster.getSshPort(),
            systemScriptBuilder.buildProfileCreationScript(clusterProfile),
            true
        );
        try {
            var arr = new ArrayList<Integer>(clusterProfile.size());
            for (String toParse : result.trim().split("\\R")) {
                arr.add(Integer.parseInt(toParse));
            }
            return arr;
        } catch (Exception e) {
            throw new RuntimeException("Can't create profile");
        }
    }
    
    @Override
    public boolean deleteProfile(Cluster cluster, String clusterName, List<String> usernames) {
        var bindedCluster = bindedClusterManager.getBinded(
            cluster.getId(),
            clusterName
        );
        return sshCommandSender
            .execute(
                bindedCluster.controller().host(),
                cluster.getUsername(),
                cluster.getPassword(),
                cluster.getSshPort(),
                systemScriptBuilder.buildProfileDeletionScript(usernames),
                false
            )
            .isEmpty();
    }

    @Override
    public boolean isProfileExists(Cluster cluster, String boundCluster, String username) {
        var bindedCluster = bindedClusterManager.getBinded(
            cluster.getId(),
            boundCluster);
        return !sshCommandSender
                .execute(
                    bindedCluster.controller().host(),
                    cluster.getUsername(),
                    cluster.getPassword(),
                    cluster.getSshPort(),
                    systemScriptBuilder.buildUserExistanceCheckScript(username), 
                    true)
                .contains("no such user");
    }

    @Override
    public Integer getId(ClusterProfile clusterProfile) {
        var bindedCluster = bindedClusterManager.getBinded(
            clusterProfile.getId().getCluster().getId(),
            clusterProfile.getId().getClusterName());
        var cluster = clusterProfile.getId().getCluster();
        var username = clusterProfile.getId().getUser().getUsername();
        var result = sshCommandSender.execute(
            bindedCluster.controller().host(),
            cluster.getUsername(),
            cluster.getPassword(),
            cluster.getSshPort(), 
            systemScriptBuilder.buildUserIdReceiveScript(username),
            true);
        return result.contains("no such user")==true? null: Integer.parseInt(result);
    }

    @Override
    public boolean updateLimits(Cluster cluster, String clusterName, List<Association> clusterProfile) {
        var bindedCluster = bindedClusterManager.getBinded(
            cluster.getId(),
            clusterName
        );
        var script = systemScriptBuilder.buildProfileUpdateScript(clusterProfile);
        if(script == null)
            return true;
        return sshCommandSender.execute(
            bindedCluster.controller().host(),
            cluster.getUsername(),
            cluster.getPassword(),
            cluster.getSshPort(),
            script,
            false
        ).isEmpty();
    }

}
