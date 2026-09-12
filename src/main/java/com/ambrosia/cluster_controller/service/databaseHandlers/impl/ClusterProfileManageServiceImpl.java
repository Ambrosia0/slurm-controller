package com.ambrosia.cluster_controller.service.databaseHandlers.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.cluster_controller.exception.api.AccessDoesntProvidedException;
import com.ambrosia.cluster_controller.exception.api.BindedClusterDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.ClusterDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.ClusterUnavailableException;
import com.ambrosia.cluster_controller.exception.api.ProfileAlreadyCreatedException;
import com.ambrosia.cluster_controller.exception.api.ProfileDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.UserDoesntExistException;
import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterProfileAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.filters.ClusterProfileFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.ClusterProfileResponse;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.model.entity.compositeKeys.ClusterProfileKey;
import com.ambrosia.cluster_controller.repository.ClusterProfileRepository;
import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.repository.UserRepository;
import com.ambrosia.cluster_controller.repository.specification.ClusterProfileSpecification;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileManageService;
import com.ambrosia.cluster_controller.service.mappers.ProfileMapper;
import com.ambrosia.cluster_controller.service.systemServices.SystemProfileChecker;
import com.ambrosia.cluster_controller.taskSchedulers.ProfileManager;
import com.ambrosia.cluster_controller.taskSchedulers.SchedulerHandler;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClusterProfileManageServiceImpl implements ClusterProfileManageService {
    private final UserRepository userRepository;

    private final ClusterRepository clusterRepository;

    private final ClusterProfileRepository clusterProfileRepository;

    private final ProfileMapper profileMapper;

    private final SlurmBindedClusterManager bindedClusterManager;

    private final SchedulerHandler schedulerHandler;

    private final ProfileManager profileManager;

    private final SystemProfileChecker systemProfileChecker;

    // password & username creating on cluster
    @Override
    @Transactional
    public void provideAccess(Long clusterId, String bindedCluster, List<ClusterProfileAdminRequest> request) {
        var cluster = clusterRepository.findById(clusterId)
            .orElseThrow(() -> new ClusterDoesntExistException());

        if(bindedClusterManager.getBinded(clusterId, bindedCluster) == null)
            throw new BindedClusterDoesntExistException();
        
        var ids = request
                    .stream()
                    .map(t -> t.userId())
                    .collect(Collectors.toSet());

        if(clusterProfileRepository.existsByIds(clusterId, bindedCluster, ids))
            throw new ProfileAlreadyCreatedException();

        if(!userRepository.compareSize(ids, ids.size()))
            throw new UserDoesntExistException();

        var users = userRepository.findByIds(ids)
            .stream()
            .collect(Collectors.toMap(k -> k.getId(), v -> v));

        var profiles = request
            .stream()
            .map(dto ->{
                var clusterProfile = profileMapper.toEntity(clusterId, bindedCluster, dto);
                var user = users.get(dto.userId());
                
                if(user == null)
                    throw new UserDoesntExistException(dto.userId());

                clusterProfile.setId(new ClusterProfileKey(
                    cluster, 
                    user,
                    bindedCluster
                ));
                clusterProfile.setPassword(user.getPassword());
                return clusterProfile;
            })
            .toList();

        var profileCount = clusterProfileRepository.countByClusterId(
            clusterId, 
            users.keySet()
        )
            .stream()
            .collect(Collectors.toMap(k -> k.userId(), v -> v.count()));
        ids.stream().forEach(id -> profileCount.putIfAbsent(id, 0L));

        var profileIds = provide(cluster, bindedCluster, profiles, profileCount);
        for(int i = 0; i < profileIds.size(); i++){
            profiles.get(i).setProfileId(profileIds.get(i));
        }
        clusterProfileRepository.batchInsert(profiles);
    }

    @Override
    public void updateAccessResources(Long clusterId, String bindedCluster, List<ClusterProfileAdminRequest> profileDtos) {
        var cluster = clusterRepository.findById(clusterId) 
            .orElseThrow(() -> new ClusterDoesntExistException());

        var profiles = clusterProfileRepository.findByIds(
            clusterId,
            bindedCluster,
            profileDtos.stream()
                .map(t -> t.userId())
                .collect(Collectors.toSet())
        );

        if(profiles.isEmpty() || profiles.size() != profileDtos.size())
            throw new ProfileDoesntExistException();

        for(int i = 0; i < profileDtos.size(); i++){
            profiles.get(i).setMaxSubmit(profileDtos.get(i).maxSubmit());
            profiles.get(i).setMaxTasks(profileDtos.get(i).maxTasks());
            profiles.get(i).setMaxTres(profileDtos.get(i).maxTres());
            profiles.get(i).setMaxTaskTtl(profileDtos.get(i).maxTaskLiveTime());
            profiles.get(i).setSoftLimit(profileDtos.get(i).softLimit());
            profiles.get(i).setHardLimit(profileDtos.get(i).hardLimit());
        }

        profileManager.updateProfile(
            cluster, 
            bindedCluster, 
            profiles.stream()
                .map(profileMapper::toAssociation)
                .toList()
        );
        clusterProfileRepository.batchUpdate(profiles);
    }

    @Override
    public void revokeAccess(long clusterId, String bindedClusterName, Set<Long> userIds) {
        var cluster = clusterRepository.findById(clusterId)
            .orElseThrow(() -> new ClusterDoesntExistException());
        var boundCluster = bindedClusterManager.getBinded(clusterId, bindedClusterName);

        if(boundCluster == null)
            throw new BindedClusterDoesntExistException();

        var profiles = clusterProfileRepository.findByIds(clusterId, bindedClusterName, userIds);
        
        if(profiles.isEmpty() || profiles.size() != userIds.size())
            throw new ProfileDoesntExistException();

        var profileCount = clusterProfileRepository.countByClusterId(clusterId, userIds)
            .stream()
            .collect(Collectors.toMap(k -> k.userId(), v -> v.count()));

        revoke(cluster, bindedClusterName, profiles, profileCount);

        clusterProfileRepository.batchDelete(profiles);
    }

    @Override
    public void revokeAccessFromGroup(long clusterId, long groupId, String bindedCluster) {
        var cluster = clusterRepository.findById(clusterId)
            .orElseThrow(() -> new ClusterDoesntExistException());

        var boundCluster = bindedClusterManager.getBinded(clusterId, bindedCluster);

        if(boundCluster == null)
            throw new BindedClusterDoesntExistException();
        
        var profiles = clusterProfileRepository.findByClusterIdAndGroupIdAndClusterName(clusterId, groupId, bindedCluster);

        var profileCount = clusterProfileRepository.countByClusterId(
            clusterId, 
            profiles.stream()
                .map(t -> t.getId().getUser().getId())
                .collect(Collectors.toSet())
        ).stream()
        .collect(Collectors.toMap(k -> k.userId(), v -> v.count()));

        revoke(cluster, bindedCluster, profiles, profileCount);

        clusterProfileRepository.batchDelete(profiles);
    }

    @Override
    public List<ClusterProfileResponse> getUserProfiles(long userId) {
        return clusterProfileRepository.findByUserId(userId).stream().map(profileMapper::toResponse).toList();
    }

    @Override
    public Resource downloadProfiles(Long clusterId, String bindedCluster, Set<Long> userIds) {
        var profiles = clusterProfileRepository.findByClusterIdAndBindedClusterAndProfileIds(
            clusterId,
            bindedCluster, 
            userIds);
        if(profiles.size() != userIds.size()){
            throw new ProfileDoesntExistException();
        }
        return schedulerHandler.downloadProfiles(profiles);
    }

    @Override
    public ClusterProfileResponse getUserProfile(long clusterId, long userId, String bindedCluster) {
        return profileMapper.toResponse(
            clusterProfileRepository.findById(clusterId, userId, bindedCluster)
                .orElseThrow(() -> new AccessDoesntProvidedException()));
    }

    @Override
    public Page<ClusterProfileResponse> getClusterProfiles(long clusterId, Pageable pageable) {
        return clusterProfileRepository.findByClusterId(clusterId, pageable).map(profileMapper::toResponse);
    }

    // @Override
    // public void cleanAllPermissions() {
    //     clusterProfileRepository.deleteAll();
    // }

    @Override
    public Page<ClusterProfileResponse> search(ClusterProfileFilter clusterProfileFilter, Pageable pageable) {
        var spec = Specification.<ClusterProfile>unrestricted()
            .and(ClusterProfileSpecification.clusterContains(clusterProfileFilter.clusterId()))
            .and(ClusterProfileSpecification.boundContains(clusterProfileFilter.bindedCluster()))
            .and(ClusterProfileSpecification.groupContains(clusterProfileFilter.groupId()))
            .and(ClusterProfileSpecification.usernameContains(clusterProfileFilter.username()));
        return clusterProfileRepository.findAll(spec, pageable)
            .map(profileMapper::toResponse);
    }
    
    private void consistencyRecovery(Cluster cluster){
        var page = 0;
        var pageSize = 100;
        var pageable = PageRequest.of(page, pageSize);
        Page<ClusterProfile> currentPage;

        currentPage = clusterProfileRepository.findByClusterId(cluster.getId(), pageable);
        do{
            currentPage.stream()
                .forEach(profile ->{
                    try {
                        if(!systemProfileChecker.isProfileExists(
                                profile.getId().getCluster(), 
                                profile.getId().getClusterName(), 
                                profile.getId().getUser().getUsername())){
                            clusterProfileRepository.delete(profile);
                        }
                    } catch (Exception e) {
                        log.error("Can't execute commands on cluster {}", profile.getId().getCluster().getDisplayedName());
                        throw new ClusterUnavailableException();
                    }
                });
            page++;
            pageable = PageRequest.of(page, pageSize);
        } while(currentPage.hasNext());
    }

    private void revoke(Cluster cluster, String bindedCluster, List<ClusterProfile> profiles, Map<Long, Long> profileCount){
        var profilesToDelete = profiles
            .stream()
            .filter(t -> profileCount.get(t.getId().getUser().getId()) - 1 == 0)
            .map(profileMapper::toDelete)
            .toList();


        var revokedAssocs = profiles.stream()
                .map(profileMapper::toAssociation)
                .toList();

        profileManager.revokeClusterAccess(
            cluster, 
            bindedCluster,
            revokedAssocs
        );
        try {
            profileManager.deleteProfile(
                cluster,
                profilesToDelete
            );
        } catch (Exception e) { // compensation
            log.error("Can't access! {}", e);
            profileManager.provideClusterAccess(
                cluster, 
                bindedCluster,
                revokedAssocs
            );
            throw e;
        }
    }

    private List<Integer> provide(Cluster cluster, String bindedCluster, List<ClusterProfile> profiles, Map<Long, Long> profileCount){
        var profilesToCreate = profiles
            .stream()
            .filter(t -> profileCount.get(t.getId().getUser().getId()) == 0)
            .map(profileMapper::toProfile)
            .toList();
        
        profileManager.createProfile(cluster, profilesToCreate);

        try {
            return profileManager.provideClusterAccess(
                cluster, 
                bindedCluster,
                profiles.stream()
                    .map(profileMapper::toAssociation)
                    .toList()
            );
        } catch (Exception e) {
            log.error("Can't provide access!", e);
            profileManager.deleteProfile(
                cluster,
                profilesToCreate
                    .stream()
                    .map(t -> t.username().toLowerCase())
                    .toList()
            );
            throw e;
        }
    }

}
