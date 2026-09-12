package com.ambrosia.cluster_controller.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.ambrosia.cluster_controller.BaseIntegrationTest;
import com.ambrosia.cluster_controller.exception.api.ClusterDoesntExistException;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterUserService;
import com.ambrosia.cluster_controller.util.TestUtils;
import com.ambrosia.cluster_controller.util.creators.ClusterProfileCreator;
import com.ambrosia.cluster_controller.util.creators.TestClusterCreator;

@Import ({ClusterProfileCreator.class, TestClusterCreator.class})
public class ClusterUserServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired ClusterUserService clusterUserService;

    @Autowired ClusterProfileCreator clusterProfileCreator;

    @Autowired TestClusterCreator testClusterCreator;

    Cluster cluster;

    @BeforeAll 
    void init(){
        cluster = testClusterCreator.create();    
    }

    @Test 
    void shouldThrowClusterDoesntExistOnTresGet(){
        assertThrows(
            ClusterDoesntExistException.class,
            () -> clusterUserService.getTres(TestUtils.randLong())
        );
    }

    @Test 
    void shouldReturnTres(){
        assertDoesNotThrow(
            () -> {
                var res = clusterUserService.getTres(cluster.getId());
                res.getFirst();
            }
        );
    }

    @Test 
    void shouldNotThrowException(){
        var bound = testClusterCreator.getBindedCluster(cluster.getId());
        var profile = clusterProfileCreator.createProfile(
            cluster,
            bound,
            null
        );
        assertFalse(clusterUserService.getAvailableClusters(profile.getId().getUser().getId()).isEmpty());
        assertFalse(clusterUserService.getBindedClusters(cluster.getId(), profile.getId().getUser().getId()).isEmpty());
    }

    @AfterAll  
    void cleanUp(){
        testClusterCreator.cleanUp(cluster.getId());
    }
}
