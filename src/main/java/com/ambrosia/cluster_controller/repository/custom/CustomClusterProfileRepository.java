package com.ambrosia.cluster_controller.repository.custom;

import java.util.List;

import com.ambrosia.cluster_controller.model.entity.ClusterProfile;

public interface CustomClusterProfileRepository {
    void batchInsert(List<ClusterProfile> clusterProfiles);
    void batchUpdate(List<ClusterProfile> clusterProfiles);
    void batchDelete(List<ClusterProfile> clusterProfiles);
}
