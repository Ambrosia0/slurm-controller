package com.ambrosia.cluster_controller.integration;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.ambrosia.cluster_controller.BaseIntegrationTest;
import com.ambrosia.cluster_controller.exception.api.AccessDoesntProvidedException;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.JobRequest;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterManageService;
import com.ambrosia.cluster_controller.service.databaseHandlers.UserTaskService;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;
import com.ambrosia.cluster_controller.util.Role;
import com.ambrosia.cluster_controller.util.TaskStatus;
import com.ambrosia.cluster_controller.util.creators.ClusterProfileCreator;
import com.ambrosia.cluster_controller.util.creators.TestClusterCreator;
import com.ambrosia.cluster_controller.util.creators.UserCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import({ClusterProfileCreator.class, UserCreator.class, TestClusterCreator.class})
public class UserTaskServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired UserTaskService userTaskService;

    @Autowired ClusterManageService clusterManageService;

    @Autowired SlurmBindedClusterManager slurmBindedClusterManager;

    @Autowired UserCreator userCreator;

    @Autowired TestClusterCreator testClusterCreator;

    @Autowired ClusterProfileCreator clusterProfileCreator;

    @Autowired ObjectMapper objectMapper;

    Cluster cluster;

    @BeforeAll 
    void init(){
        cluster = testClusterCreator.create();    
    }

    @Test 
    void shouldThrowAccessDoesntProvidedOnTaskCreate(){
        var user = userCreator.createUser(Role.ROLE_USER);
        assertThrows(
            AccessDoesntProvidedException.class,
            () -> userTaskService.createTask(
                cluster.getId(), 
                testClusterCreator.getBindedCluster(cluster.getId()),
                user.getId(), 
                createTaskRequest()
            )
        );
    }

    @Test
    void shouldCreateTaskThenExecuteTask(){
        var profile = createProfile();
        var clusterName = testClusterCreator.getBindedCluster(cluster.getId());
        var userId = profile.getId().getUser().getId();
        var filter = new TaskFilter();
        filter.setTaskStatus(Set.of(TaskStatus.COMPLETED));
        filter.setClusterNames(Set.of(clusterName));
        assertDoesNotThrow(
            () -> {
                userTaskService.createTask(
                    cluster.getId(), 
                    clusterName,
                    userId,
                    createTaskRequest()
                );
                await()
                    .atMost(Duration.ofMinutes(1))
                    .pollInterval(Duration.ofSeconds(5))
                    .untilAsserted(
                        () -> assertFalse(
                            userTaskService.getTasks(
                                cluster.getId(),
                                userId,
                                filter
                            )
                            .isEmpty()
                        )
                    );
            }
        );
    }

    @Test 
    void shouldCreateTaskThenCancelTask(){
        var profile = createProfile();
        var clusterName = testClusterCreator.getBindedCluster(cluster.getId());
        var userId = profile.getId().getUser().getId();

        assertDoesNotThrow(
            () -> {
                var task = userTaskService.createTask(
                    cluster.getId(), 
                    clusterName,
                    userId,
                    createLongTaskRequest()
                );
                userTaskService.cancelTask(
                    cluster.getId(), 
                    userId, 
                    clusterName, 
                    task.jobId()
                );
                await()
                    .atMost(Duration.ofMinutes(1))
                    .pollInterval(Duration.ofSeconds(5))
                    .untilAsserted(
                        () -> assertFalse(
                            userTaskService.pollTasks(
                                cluster.getId(), 
                                clusterName,
                                userId,
                                new TaskPollFilter(null)
                            )
                            .isEmpty()
                        )
                    );
            }
        );
    }

    @AfterEach 
    void eachCleanUp(){
        userCreator.deleteAll();
    }

    @AfterAll  
    void cleanUp(){
        testClusterCreator.cleanUp(cluster.getId());
    }

    ClusterProfile createProfile(){
        return clusterProfileCreator.createProfile(
            cluster, 
            testClusterCreator.getBindedCluster(cluster.getId()),
            null
        );
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

    TaskRequest createLongTaskRequest() throws Exception{
        return TaskRequest.builder()
            .job(JobRequest.builder()
                .script(Files.readString(Path.of("src/test/resources/long_test_script.sh")))
                .maxNodes(1)
                .jobName("test_job")
                .numberOfTasks(1)
                .build()
            )
            .build();
    }
}
