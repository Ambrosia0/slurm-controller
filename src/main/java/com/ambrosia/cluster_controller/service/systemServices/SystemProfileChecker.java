package com.ambrosia.cluster_controller.service.systemServices;

import com.ambrosia.cluster_controller.model.entity.Cluster;

public interface SystemProfileChecker {
    boolean isProfileExists(Cluster cluster, String boundCluster, String username);
}
