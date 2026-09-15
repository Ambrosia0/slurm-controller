package com.ambrosia.cluster_controller.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ambrosia.cluster_controller.controller.api.user.UserTaskController;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.JobRequest;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.service.databaseHandlers.UserTaskService;
import com.ambrosia.cluster_controller.util.TestSecurityConfiguration;
import com.ambrosia.cluster_controller.util.TestUtils;
import com.ambrosia.cluster_controller.util.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles ("test")
@Import(TestSecurityConfiguration.class)
@WebMvcTest(UserTaskController.class) 
public class UserTaskControllerTest {
    @Autowired MockMvc mockMvc;

    @MockitoBean UserTaskService userTaskService;

    @Autowired ObjectMapper objectMapper;

    String TEST_SCRIPT = """
    #!/bin/sh
    
    sleep 60;
    """;

    JobRequest unlimitedJob = JobRequest.builder()
        .cpusPerTask(0)
        .maxNodes(0)
        .maxTaskLiveTime(0)
        .build();

    
    @WithMockCustomUser
    @Test 
    void shouldNotThrowExceptionOnGetUserTasks() throws Exception{
        when(userTaskService.getTasks(anyLong(),anyLong(), any(TaskFilter.class)))
            .thenReturn(List.of());
        mockMvc.perform(get("/api/cluster/{clusterId}/task", TestUtils.randLong()))
            .andExpect(status().is2xxSuccessful());
    }

    @WithAnonymousUser 
    @Test 
    void shouldReturnUnauthorizedStatusOnGetUserTaskForAnonymous() throws Exception{
        mockMvc.perform(
            get("/api/cluster/{clusterId}/task", TestUtils.randLong())
        ).andExpect(status().is4xxClientError());
    }

    @WithMockCustomUser
    @Test 
    void shouldNotThrowExceptionOnPollTasks() throws Exception{
        when(userTaskService.pollTasks(anyLong(), anyString(), anyLong(), any(TaskPollFilter.class)))
            .thenReturn(List.of());
        mockMvc.perform(
            get(
                "/api/cluster/{clusterId}/bound/{clusterName}/task", 
                TestUtils.randLong(), 
                TestUtils.randName()
            )
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockCustomUser
    @Test 
    void shouldNotThrowExceptionOnTaskCreate() throws Exception {
        mockMvc.perform(post("/api/cluster/{clusterId}/bound/{clusterName}/task", 
                TestUtils.randLong(), 
                TestUtils.randName()
            )
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(TaskRequest.builder()
                .script(TEST_SCRIPT)
                .job(unlimitedJob)
                .build()
            ))
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockCustomUser
    @Test
    void shouldNotThrowExceptionOnTaskCancel() throws Exception {
        mockMvc.perform(
            delete("/api/cluster/{clusterId}/bound/{clusterName}/task/{taskId}",
                TestUtils.randLong(),
                TestUtils.randName(),
                TestUtils.randLong()
            )
        );
    }
}
