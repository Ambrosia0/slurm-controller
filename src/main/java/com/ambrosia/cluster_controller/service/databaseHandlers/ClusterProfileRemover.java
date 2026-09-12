package com.ambrosia.cluster_controller.service.databaseHandlers;

/**
 * Deletes related profiles
 * ClusterProfileRemover
 */
public interface ClusterProfileRemover {
    void removeProfilesByClusterId(Long clusterId);
    void removeProfilesByGroupId(Long groupId);
    void removeProfilesByUserId(Long userId);
}
