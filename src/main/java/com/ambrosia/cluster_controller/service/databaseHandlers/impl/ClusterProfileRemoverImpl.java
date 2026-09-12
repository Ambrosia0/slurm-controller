package com.ambrosia.cluster_controller.service.databaseHandlers.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.repository.ClusterProfileRepository;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileRemover;
import com.ambrosia.cluster_controller.service.mappers.ProfileMapper;
import com.ambrosia.cluster_controller.taskSchedulers.ProfileManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class ClusterProfileRemoverImpl implements ClusterProfileRemover{
    private final ClusterProfileRepository clusterProfileRepository;

    private final ProfileManager profileManager;

    private final ProfileMapper profileMapper;
    
    @Override
    public void removeProfilesByClusterId(Long clusterId) {
        delete(clusterProfileRepository.findByClusterId(clusterId));
    }

    @Override
    public void removeProfilesByGroupId(Long groupId) {
        delete(clusterProfileRepository.findByGroupId(groupId));
    }

    @Override
    public void removeProfilesByUserId(Long userId) {
        delete(clusterProfileRepository.findByUserId(userId));
    }

    private void delete(List<ClusterProfile> profiles){
        if(profiles.isEmpty())
            return;
        deleteChain(profiles);
        clusterProfileRepository.batchDelete(profiles);
    }

    private void deleteChain(List<ClusterProfile> profiles){
        profiles.stream()
            .collect(Collectors.groupingBy(t -> t.getId().getCluster().getId()))
            .forEach((id, unpartitionedProfiles) ->{
                unpartitionedProfiles.stream()
                    .collect(Collectors.groupingBy(t -> t.getId().getClusterName()))
                    .forEach((clusterName, profileList) -> {
                        var cluster = profileList.getFirst().getId().getCluster();
                        profileManager.revokeClusterAccess(
                            cluster, 
                            clusterName,
                            profileList.stream()
                                .map(profileMapper::toAssociation)
                                .toList()
                        );
                        profileManager.deleteProfile(
                            cluster,
                            profileList.stream()
                                .map(profileMapper::toDelete)
                                .toList()
                        );
                    });
            });
    }
}
