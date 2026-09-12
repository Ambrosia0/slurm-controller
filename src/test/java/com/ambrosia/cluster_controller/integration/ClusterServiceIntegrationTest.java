package com.ambrosia.cluster_controller.integration;


import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.cluster_controller.BaseIntegrationTest;
import com.ambrosia.cluster_controller.exception.api.ClusterDoesntExistException;
import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.filters.ClusterFilter;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterManageService;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;
import com.ambrosia.cluster_controller.util.HttpSchema;
import com.ambrosia.cluster_controller.util.SupportedTaskSchedulers;
import com.ambrosia.cluster_controller.util.factory.ClusterFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

public class ClusterServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired ClusterManageService clusterService;

    @Autowired ClusterRepository clusterRepository;

    @Autowired SlurmBindedClusterManager slurmBindedClusterManager;

    @AfterEach
    void cleanUp(){
        clusterRepository.deleteAll();
    }

    @Test 
    void shouldThrowClusterDoesntExistOnCreate(){
        assertThrows(
            ClusterDoesntExistException.class, 
            () -> clusterService.addCluster(
                new ClusterAdminRequest(
                    "test",
                    "test",
                    "test",
                    "test",
                    null,
                    HttpSchema.HTTP,
                    22,
                    SupportedTaskSchedulers.SLURMv0040
                )
            )
        );
    }
    

    @Test 
    void shouldAddCluster(){
        assertDoesNotThrow(
            () -> clusterService.addCluster(
                TestClusterConnectionFactory.createRequest()
            )
        );
    }

    @Test
    void shouldThrowClusterDoesntExistOnDelete(){
        assertThrows(
            ClusterDoesntExistException.class, 
            () -> clusterService.deleteCluster(randLong())
        );
    }

    @Test
    void shouldDeleteCluster(){
        clusterService.addCluster(TestClusterConnectionFactory.createRequest());
        assertDoesNotThrow(
            () -> clusterService.deleteCluster(clusterRepository.findAll().getFirst().getId())
        );
    }

    @Test
    void shouldReturnClusters(){
        var cluster = createCluster();
        assertNotEquals(
            0, 
            clusterService.getClusters(
                    new ClusterFilter(cluster.getDisplayedName().substring(0, 3)),
                    PageRequest.of(0, 10)
                )
                .getContent().size()
        );
    }

    @Test
    void shouldThrowClusterDoesntExistExceptionOnGetBinded(){
        assertThrows(
            ClusterDoesntExistException.class,
            () -> clusterService.getBindedClusters(randLong())
        );
    }

    @Test
    void shouldReturnBoundClusters(){
        var cluster = clusterService.addCluster(TestClusterConnectionFactory.createRequest());
        await().atMost(Duration.ofMinutes(2)).pollInterval(Duration.ofSeconds(5))
            .untilAsserted(() -> 
                assertDoesNotThrow(() -> clusterService.getBindedClusters(cluster.id()).getFirst())
            );
    }

    private Long randLong(){
        return ThreadLocalRandom.current().nextLong();
    }

    private Cluster createCluster(){
        return clusterRepository.saveAndFlush(ClusterFactory.create());
    }
}
