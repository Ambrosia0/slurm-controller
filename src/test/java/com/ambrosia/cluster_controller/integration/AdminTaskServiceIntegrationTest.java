
package com.ambrosia.cluster_controller.integration;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.ambrosia.cluster_controller.BaseIntegrationTest;
import com.ambrosia.cluster_controller.exception.api.ClusterDoesntExistException;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.JobRequest;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.service.databaseHandlers.AdminTaskService;
import com.ambrosia.cluster_controller.util.TaskStatus;
import com.ambrosia.cluster_controller.util.TestUtils;
import com.ambrosia.cluster_controller.util.creators.TestClusterCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import(TestClusterCreator.class)
public class AdminTaskServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired TestClusterCreator testClusterCreator;

    @Autowired AdminTaskService adminTaskService;

    @Autowired ObjectMapper objectMapper;

    Cluster cluster;
    
    @BeforeAll 
    void init(){
        cluster = testClusterCreator.create();    
    }

    @Test 
    void shouldThrowClusterDoesntExistOnCreateTask(){
        assertThrows(
            ClusterDoesntExistException.class,
            () -> adminTaskService.createTask(
                TestUtils.randLong(),
                TestUtils.randName(),
                createTaskRequest()
            )
        );
    }

    @Test 
    void shouldCreateTaskThenCompleteTask(){
        var bound = testClusterCreator.getBindedCluster(cluster.getId());
        var filter = new TaskFilter();
        filter.setClusterNames(Set.of(bound));
        filter.setUsernames(List.of(cluster.getUsername()));
        filter.setTaskStatus(Set.of(TaskStatus.COMPLETED));
        assertDoesNotThrow(
            () -> {
                adminTaskService.createTask(
                    cluster.getId(), 
                    bound,
                    createTaskRequest()
                );
                await()
                    .atMost(Duration.ofMinutes(1))
                    .pollInterval(Duration.ofSeconds(5))
                    .untilAsserted(
                        () -> assertFalse(
                            adminTaskService.getTasks(cluster.getId(), filter).isEmpty()
                        )
                    );
            }
        );
    }

    @Test 
    void shouldThrowClusterDoesntExistOnCancelTask(){
        assertThrows(
            ClusterDoesntExistException.class,
            () -> adminTaskService.cancelTask(
                TestUtils.randLong(),
                TestUtils.randName(),
                TestUtils.randLong()
            )
        );
    }

    @Test 
    void shouldCreateTaskThenCancelTask(){
        var bound = testClusterCreator.getBindedCluster(cluster.getId());
        assertDoesNotThrow(
            () -> {
                var task = adminTaskService.createTask(
                    cluster.getId(), 
                    bound,
                    createTaskRequest()
                );
                adminTaskService.cancelTask(
                    cluster.getId(), 
                    bound,
                    task.jobId()
                );
                await()
                    .atMost(Duration.ofMinutes(1))
                    .pollInterval(Duration.ofSeconds(5))
                    .untilAsserted(
                        () -> assertFalse(
                            adminTaskService.pollTasks(cluster.getId(), bound, new TaskPollFilter(null))
                                .stream()
                                .filter(t -> t.jobState().contains(TaskStatus.CANCELLED.name()) && t.username().equalsIgnoreCase(cluster.getUsername()))
                                .toList()
                                .isEmpty()
                        )
                    );
            }
        );
    }

    @Test 
    void shouldThrowClusterDoesntExistOnGetTasks(){
        assertThrows(
            ClusterDoesntExistException.class,
            () -> adminTaskService.cancelTask(
                TestUtils.randLong(),
                TestUtils.randName(),
                TestUtils.randLong()
            )
        );
    }

    @AfterAll  
    void cleanUp(){
        testClusterCreator.cleanUp(cluster.getId());
    }

    TaskRequest createTaskRequest() throws Exception{
        return TaskRequest.builder()
            .job(JobRequest.builder()
                .script(Files.readString(Path.of("src/test/resources/test_script.sh")))
                .maxNodes(1)
                .jobName("test_job")
                .numberOfTasks(1)
                .build()
            )
            .build();
    }
}
