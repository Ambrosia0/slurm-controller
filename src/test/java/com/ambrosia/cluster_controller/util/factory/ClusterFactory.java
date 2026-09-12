package com.ambrosia.cluster_controller.util.factory;

import com.ambrosia.cluster_controller.model.entity.Cluster;

public class ClusterFactory {
    public static Cluster create(){
        return Cluster.builder()
            .host("test")
            .username("testusername")
            .displayedName("Test")
            .build();
    }
}
