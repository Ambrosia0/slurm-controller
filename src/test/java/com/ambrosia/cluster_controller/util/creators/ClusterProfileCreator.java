package com.ambrosia.cluster_controller.util.creators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.model.entity.Group;
import com.ambrosia.cluster_controller.repository.ClusterProfileRepository;
import com.ambrosia.cluster_controller.repository.UserRepository;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileManageService;
import com.ambrosia.cluster_controller.util.Role;
import com.ambrosia.cluster_controller.util.factory.ClusterProfileRequestFactory;
import com.ambrosia.cluster_controller.util.factory.UserFactory;

import jakarta.annotation.Nullable;

@TestComponent
public class ClusterProfileCreator {
    @Autowired ClusterProfileRepository clusterProfileRepository;

    @Autowired ClusterProfileManageService clusterProfileManageService;

    @Autowired UserRepository userRepository;

    @Autowired TextEncryptor textEncryptor;

    public ClusterProfile createProfile(Cluster cluster, String clusterName, @Nullable Group group){
        var us = userRepository.saveAndFlush(UserFactory.create(Role.ROLE_USER, group, textEncryptor));

        clusterProfileManageService.provideAccess(
            cluster.getId(), 
            clusterName,
            List.of(ClusterProfileRequestFactory.create(us.getId()))
        );

        return clusterProfileRepository.findById(cluster.getId(), us.getId(), clusterName)
            .orElseThrow(() -> new RuntimeException("Can't create user!"));
    }
}
