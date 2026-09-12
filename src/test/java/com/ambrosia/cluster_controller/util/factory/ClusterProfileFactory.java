package com.ambrosia.cluster_controller.util.factory;

import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.model.entity.compositeKeys.ClusterProfileKey;

public class ClusterProfileFactory {
    public static ClusterProfile create(Cluster cluster, String clusterName, User user){
        return ClusterProfile.builder()
            .id(ClusterProfileKey.builder()
                    .cluster(cluster)
                    .user(user)
                    .clusterName(clusterName)
                    .build()
            )
            .build();
    }
}
