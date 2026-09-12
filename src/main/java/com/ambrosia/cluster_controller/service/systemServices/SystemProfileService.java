package com.ambrosia.cluster_controller.service.systemServices;

import java.util.List;

import com.ambrosia.cluster_controller.model.DTO.scheduler.Association;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;

/**
 * Service for managing user profiles on the operating system
 * 
 * ProfileService
 */
public interface SystemProfileService {
    List<Integer> createProfile(Cluster cluster, String clusterName, List<Association> clusterProfile);
    boolean deleteProfile(Cluster cluster, String clusterName, List<String> usernames);
    Integer getId(ClusterProfile clusterProfile);
    boolean updateLimits(Cluster cluster, String clusterName, List<Association> clusterProfile);
}
