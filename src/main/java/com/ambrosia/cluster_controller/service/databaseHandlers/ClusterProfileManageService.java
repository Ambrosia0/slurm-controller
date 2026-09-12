package com.ambrosia.cluster_controller.service.databaseHandlers;

import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterProfileAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.filters.ClusterProfileFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.ClusterProfileResponse;

/**
 * Service for managing clusterpPro
 */
public interface ClusterProfileManageService {

    /**
     * Provides access to bound to slurmdbd cluster
     * @param profileDtos profiles
     */
    void provideAccess(Long clusterId, String bindedCluster, List<ClusterProfileAdminRequest> profileDtos);

    /**
     * Updates available resources for profiles
     * @param profileDtos profiles
     */
    void updateAccessResources(Long clusterId, String bindedCluster, List<ClusterProfileAdminRequest> profileDtos);

    /**
     * Revokes access to bound cluster for specific user
     * @param clusterId Id of entity containing connection information for slurmdbd
     * @param userId User id of whose access is being revoked
     * @param bindedCluster Name of bound to slurmdbd cluster
     */
    void revokeAccess(long clusterId, String bindedCluster, Set<Long> userIds);

    /**
     * Revokes access to bound cluster from group of users
     * @param clusterId Id of entity containing connection information for slurmdbd
     * @param groupId Id of the group of users whose access is being revoked
     * @param bindedCluster Name of bound to slurmdbd cluster
     */
    void revokeAccessFromGroup(long clusterId, long groupId, String bindedCluster);

    /**
     * Gets user profiles on connected to slurmdbd clusters
     * @param userId User id of whose cluster profiles is needed
     * @return Profiles of bound clusters
     */
    List<ClusterProfileResponse> getUserProfiles(long userId);

    /**
     * Downloads user's profile directory from connected to slurmdbd cluster
     * @param clusterId Id of entity containing connection information for slurmdbd
     * @param bindedCluster Name of bound to slurmdbd cluster
     * @param userIds User ids of whose profile is being downloaded
     * @return Resource, containing zipped user home directories
     */
    Resource downloadProfiles(Long clusterId, String bindedCluster, Set<Long> userIds);

    /**
     * Gets specific user profile on bound to slurmdbd cluster
     * @param clusterId
     * @param userId
     * @param bindedCluster
     * @return
     */
    ClusterProfileResponse getUserProfile(long clusterId, long userId, String bindedCluster);

    /**
     * Pagination through profiles on connected to slurmdbd clusters
     * @param clusterId Id of entity containing connection information for slurmdbd
     * @param pageable pageable
     * @return Profiles on bound clusters
     */
    Page<ClusterProfileResponse> getClusterProfiles(long clusterId, Pageable pageable);

    // void cleanAllPermissions();

    /**
     * Searches for profiles on bound to slurmdbd cluster
     * @param clusterProfileFilter filters
     * @return found profiles
     */
    Page<ClusterProfileResponse> search(ClusterProfileFilter clusterProfileFilter, Pageable pageable);

}
