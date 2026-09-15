package com.ambrosia.cluster_controller.controller.admin;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ambrosia.cluster_controller.controller.api.admin.AdminTaskController;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.JobRequest;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.service.databaseHandlers.AdminTaskService;
import com.ambrosia.cluster_controller.util.TestSecurityConfiguration;
import com.ambrosia.cluster_controller.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@Import(TestSecurityConfiguration.class)
@WebMvcTest(AdminTaskController.class)
public class AdminTaskControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean AdminTaskService adminTaskService;
    
    String TEST_SCRIPT = """
    #!/bin/sh
    
    sleep 60;
    """;

    JobRequest unlimitedJob = JobRequest.builder()
        .cpusPerTask(0)
        .maxNodes(0)
        .maxTaskLiveTime(0)
        .build();

    @WithMockUser
    @Test 
    void shouldThrowForbiddenExceptionOnGet() throws Exception{
        mockMvc.perform(get("/api/admin/cluster/{clusterId}", TestUtils.randLong()))
            .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnGet() throws Exception{
        when(adminTaskService.getTasks(anyLong(), any(TaskFilter.class)))
            .thenReturn(List.of());
        mockMvc.perform(get("/api/admin/cluster/{clusterId}", TestUtils.randLong()))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionPollTasks() throws Exception{
        when(adminTaskService.pollTasks(anyLong(), anyString(), any(TaskPollFilter.class)))
            .thenReturn(List.of());
        mockMvc.perform(get(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/task", 
            TestUtils.randLong(),
            TestUtils.randName()
        ))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionCreateTask() throws Exception{
        mockMvc.perform(post(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/task", 
                TestUtils.randLong(),
                TestUtils.randName()
            )
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(TaskRequest.builder()
                .script(TEST_SCRIPT)
                .job(unlimitedJob)
                .build()
            ))
        )
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionCancelTask() throws Exception{
        mockMvc.perform(delete(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/task/{taskId}", 
                TestUtils.randLong(),
                TestUtils.randName(),
                TestUtils.randLong()
            )
        )
            .andExpect(status().is2xxSuccessful());
    }
}
