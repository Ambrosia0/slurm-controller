package com.ambrosia.cluster_controller.service.mappers;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterProfileAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.generic.ClusterProfileResponse;
import com.ambrosia.cluster_controller.model.DTO.scheduler.Association;
import com.ambrosia.cluster_controller.model.DTO.scheduler.Profile;
import com.ambrosia.cluster_controller.model.DTO.scheduler.ProfileTask;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.model.entity.compositeKeys.ClusterProfileKey;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ProfileMapper {
    private final UserMapper userMapper;
    
    private final TextEncryptor textEncryptor;


    public ClusterProfile toEntity(long clusterId, String clusterName, ClusterProfileAdminRequest dto){
        var profile = ClusterProfile.builder();

        var user = new User();
        user.setId(dto.userId());
        
        var cluster = new Cluster();
        cluster.setId(clusterId);

        profile.id(new ClusterProfileKey(cluster, user, clusterName));

        if(dto.maxSubmit() != null) profile.maxSubmit(dto.maxSubmit());
        if(dto.maxTasks() != null) profile.maxTasks(dto.maxTasks());
        if(dto.maxTres() != null && !dto.maxTres().isEmpty()) profile.maxTres(dto.maxTres());
        if(dto.softLimit() != null) profile.softLimit(dto.softLimit());
        if(dto.hardLimit() != null) profile.hardLimit(dto.hardLimit());
        if(dto.maxTaskLiveTime() != null) profile.maxTaskTtl(dto.maxTaskLiveTime());
        
        return profile.build();
    }

    public Association toAssociation(ClusterProfile clusterProfile){
        return new Association(
            clusterProfile.getId().getUser().getUsername().toLowerCase(),
            textEncryptor.decrypt(clusterProfile.getPassword()),
            clusterProfile.getMaxSubmit(),
            clusterProfile.getMaxTasks(),
            clusterProfile.getMaxTres(),
            clusterProfile.getMaxTaskTtl(),
            clusterProfile.getSoftLimit(),
            clusterProfile.getHardLimit()
        );
    }

    public Profile toProfile(ClusterProfile clusterProfile){
        return new Profile(
            clusterProfile.getId().getUser().getUsername().toLowerCase()
        );
    }

    public String toDelete(ClusterProfile clusterProfile){
        return clusterProfile.getId().getUser().getUsername().toLowerCase();
    }

    public ProfileTask toTask(ClusterProfile clusterProfile){
        return new ProfileTask(
            clusterProfile.getId().getUser().getId(),
            clusterProfile.getId().getUser().getUsername().toLowerCase(),
            clusterProfile.getPassword()
        );
    }

    public ClusterProfileResponse toResponse(ClusterProfile clusterProfile){
        var dto = new ClusterProfileResponse(
            clusterProfile.getId().getCluster().getId(), 
            userMapper.toAdminResponse(clusterProfile.getId().getUser()),
            clusterProfile.getId().getClusterName(),
            clusterProfile.getProfileId(),
            clusterProfile.getMaxSubmit(),
            clusterProfile.getMaxTasks(),
            clusterProfile.getMaxTres(),
            clusterProfile.getSoftLimit(),
            clusterProfile.getHardLimit(),
            clusterProfile.getMaxTaskTtl(),
            clusterProfile.getCreatedAt());
        return dto;
    }
}
