package com.ambrosia.cluster_controller.util.creators;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.cluster_controller.BaseIntegrationTest.TestClusterConnectionFactory;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterManageService;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;

@TestComponent 
public class TestClusterCreator {
    @Autowired ClusterRepository clusterRepository;

    @Autowired ClusterManageService clusterManageService;

    @Autowired SlurmBindedClusterManager slurmBindedClusterManager;

    public Cluster create(){
        var conn = TestClusterConnectionFactory.createRequest();
        var resp = clusterManageService.addCluster(conn);

        await().atMost(Duration.ofMinutes(2)).pollInterval(Duration.ofSeconds(5))
            .untilAsserted(
                () -> assertFalse(slurmBindedClusterManager.getBindedClusters(resp.id()).isEmpty())
            );
        return clusterRepository.findById(resp.id())
            .orElseThrow(() -> new RuntimeException("Can't create cluster!"));
    }

    public String getBindedCluster(Long clusterId){
        return slurmBindedClusterManager.getBindedClusters(clusterId).getFirst().name();
    }

    public void cleanUp(Long clusterId){
        clusterManageService.deleteCluster(clusterId);
    }
}
