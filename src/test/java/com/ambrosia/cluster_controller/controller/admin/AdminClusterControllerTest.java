package com.ambrosia.cluster_controller.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ambrosia.cluster_controller.controller.api.admin.AdminClusterController;
import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.ClusterAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.ClusterFilter;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterManageService;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileManageService;
import com.ambrosia.cluster_controller.util.HttpSchema;
import com.ambrosia.cluster_controller.util.SupportedTaskSchedulers;
import com.ambrosia.cluster_controller.util.TestSecurityConfiguration;
import com.ambrosia.cluster_controller.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@Import(TestSecurityConfiguration.class)
@WebMvcTest(AdminClusterController.class) 
public class AdminClusterControllerTest {
    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @MockitoBean ClusterManageService clusterManageService;
    @MockitoBean ClusterProfileManageService clusterProfileManageService;

    ClusterAdminRequest clusterAdminRequest = ClusterAdminRequest.builder()
        .daemonPort(6820)
        .displayedName("test")
        .host("test-host")
        .password("testpassword")
        .sshPort(22)
        .taskScheduler(SupportedTaskSchedulers.SLURMv0042)
        .username("testusername")
        .schema(HttpSchema.HTTP)
        .build();

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnGetClusters() throws Exception{
        when(clusterManageService.getClusters(any(ClusterFilter.class), any(Pageable.class)))
            .thenReturn(Page.empty());
        mockMvc.perform(get("/api/admin/cluster"))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnAddCluster() throws Exception{
        when(clusterManageService.addCluster(any(ClusterAdminRequest.class)))
            .thenReturn(ClusterAdminResponse.builder().build());
        mockMvc.perform(post("/api/admin/cluster")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(clusterAdminRequest))
        )
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnDeleteCluster() throws Exception{
        when(clusterManageService.addCluster(any(ClusterAdminRequest.class)))
            .thenReturn(ClusterAdminResponse.builder().build());
        mockMvc.perform(delete("/api/admin/cluster/{clusterId}", TestUtils.randLong()))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnGetSupportedSchedulers() throws Exception{
        mockMvc.perform(get("/api/admin/cluster/versions"))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnGetBoundClusters() throws Exception{
        when(clusterManageService.getBindedClusters(anyLong()))
            .thenReturn(List.of());
        mockMvc.perform(get("/api/admin/cluster/{clusterId}/bound", TestUtils.randLong()))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnDeleteGroupProfiles() throws Exception{
        when(clusterManageService.getBindedClusters(anyLong()))
            .thenReturn(List.of());
        mockMvc.perform(delete(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/groups/{groupId}",
            TestUtils.randLong(),
            TestUtils.randName(),
            TestUtils.randLong()
        ))
            .andExpect(status().is2xxSuccessful());
    }
}
