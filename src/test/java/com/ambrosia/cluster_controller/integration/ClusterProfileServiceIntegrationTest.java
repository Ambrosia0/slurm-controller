package com.ambrosia.cluster_controller.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import com.ambrosia.cluster_controller.BaseIntegrationTest;
import com.ambrosia.cluster_controller.exception.api.BindedClusterDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.ClusterDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.UserDoesntExistException;
import com.ambrosia.cluster_controller.model.DTO.filters.ClusterProfileFilter;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.repository.GroupRepository;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterManageService;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileManageService;
import com.ambrosia.cluster_controller.service.systemServices.SystemProfileChecker;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmProfileChecker;
import com.ambrosia.cluster_controller.util.Role;
import com.ambrosia.cluster_controller.util.TestUtils;
import com.ambrosia.cluster_controller.util.creators.TestClusterCreator;
import com.ambrosia.cluster_controller.util.creators.UserCreator;
import com.ambrosia.cluster_controller.util.factory.ClusterProfileRequestFactory;
import com.ambrosia.cluster_controller.util.factory.GroupFactory;

@Import({UserCreator.class, TestClusterCreator.class})
public class ClusterProfileServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired ClusterManageService clusterManageService;

    @Autowired ClusterProfileManageService clusterProfileService;

    @Autowired GroupRepository groupRepository;

    @Autowired SlurmBindedClusterManager slurmBindedClusterManager;

    @Autowired SystemProfileChecker systemProfileChecker;

    @Autowired SlurmProfileChecker slurmProfileChecker;

    @Autowired ClusterRepository clusterRepository;

    @Autowired UserCreator userCreator;

    @Autowired TestClusterCreator testClusterCreator;
    Cluster cluster;

    @BeforeAll 
    void init(){
        cluster = testClusterCreator.create();
    }

    @Test 
    void shouldThrowUserDoesntExistExceptionOnAccessProvide(){
        assertThrows(
            UserDoesntExistException.class,
            () -> clusterProfileService.provideAccess(
                cluster.getId(),
                slurmBindedClusterManager.getBindedClusters(cluster.getId()).getFirst().name(),
                List.of(ClusterProfileRequestFactory.create(TestUtils.randLong()))
            )
        );
    }

    @Test
    void shouldThrowClusterDoesntExistExceptionOnAccessProvide(){
        var user = userCreator.createUser(Role.ROLE_USER);
        assertThrows(
            ClusterDoesntExistException.class,
            () -> clusterProfileService.provideAccess(
                TestUtils.randLong(),
                slurmBindedClusterManager.getBindedClusters(cluster.getId()).getFirst().name(),
                List.of(ClusterProfileRequestFactory.create(user.getId()))
            )
        );
    }

    @Test
    void shouldThrowBindedClusterDoesntExistExceptionOnAccessProvide(){
        var user = userCreator.createUser(Role.ROLE_USER);
        assertThrows(
            BindedClusterDoesntExistException.class,
            () -> clusterProfileService.provideAccess(
                cluster.getId(),
                TestUtils.randName(),
                List.of(ClusterProfileRequestFactory.create(user.getId()))
            )
        );
    }

    @Test
    void shouldCreateProfileThenDeleteProfile(){
        var user = userCreator.createUser(Role.ROLE_USER);
        var bound = slurmBindedClusterManager.getBindedClusters(cluster.getId()).getFirst().name();
        assertDoesNotThrow(
            () -> clusterProfileService.provideAccess(
                cluster.getId(),
                bound,
                List.of(ClusterProfileRequestFactory.create(user.getId()))
            )
        );
        assertTrue(systemProfileChecker.isProfileExists(cluster, bound, user.getUsername()));
        assertTrue(slurmProfileChecker.isProfileExist(cluster, bound, user.getUsername()));
        assertDoesNotThrow(
            () -> clusterProfileService.revokeAccess(
                cluster.getId(),
                bound,
                Set.of(user.getId())
            )
        );
        assertFalse(systemProfileChecker.isProfileExists(cluster, bound, user.getUsername()));
        assertFalse(slurmProfileChecker.isProfileExist(cluster, bound, user.getUsername()));
    }

    @Test 
    void shouldRevokeAccessFromGroup(){
        var user = createGroupWithUser();
        var bound = slurmBindedClusterManager.getBindedClusters(cluster.getId()).getFirst().name();
        assertDoesNotThrow(
            () -> clusterProfileService.provideAccess(
                cluster.getId(),
                bound,
                List.of(ClusterProfileRequestFactory.create(user.getId()))
            )
        );
        assertDoesNotThrow(
            () -> clusterProfileService.revokeAccessFromGroup(
                cluster.getId(), 
                user.getGroup().getId(),
                bound
            )
        );
        assertFalse(systemProfileChecker.isProfileExists(cluster, bound, user.getUsername()));
        assertFalse(slurmProfileChecker.isProfileExist(cluster, bound, user.getUsername()));
    }
    
    @Test 
    void shouldDownloadProfile(){
        var user = userCreator.createUser(Role.ROLE_USER);
        var bound = slurmBindedClusterManager.getBindedClusters(cluster.getId()).getFirst().name();
        assertDoesNotThrow(
            () -> clusterProfileService.provideAccess(
                cluster.getId(),
                bound,
                List.of(ClusterProfileRequestFactory.create(user.getId()))
            )
        );
        assertDoesNotThrow(
            () -> clusterProfileService.downloadProfiles(
                cluster.getId(),
                bound,
                Set.of(user.getId())
            )
        );
    }

    @Test
    void shouldSearchProfile(){
        var user = userCreator.createUser(Role.ROLE_USER);
        var bound = slurmBindedClusterManager.getBindedClusters(cluster.getId()).getFirst().name();
        assertDoesNotThrow(
            () -> clusterProfileService.provideAccess(
                cluster.getId(),
                bound,
                List.of(ClusterProfileRequestFactory.create(user.getId()))
            )
        );

        assertNotEquals(
            0,
            clusterProfileService.search(
                ClusterProfileFilter.builder()
                    .clusterId(cluster.getId())
                    .build(),
                PageRequest.of(0,10)
            ).getContent().size()
        );
    }

    @AfterEach
    void cleanUp(){
        userCreator.deleteAll();
        groupRepository.deleteAll();
    }

    @AfterAll
    void clusterDelete(){
        testClusterCreator.cleanUp(cluster.getId());
    }


    private User createGroupWithUser(){
        var group = groupRepository.saveAndFlush(GroupFactory.create());
        return userCreator.createUser(Role.ROLE_USER, group);
    }
}
