package com.ambrosia.cluster_controller.controller.user;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ambrosia.cluster_controller.controller.api.user.UserClusterController;
import com.ambrosia.cluster_controller.model.DTO.generic.ClusterProfileResponse;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileManageService;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterUserService;
import com.ambrosia.cluster_controller.util.TestSecurityConfiguration;
import com.ambrosia.cluster_controller.util.security.WithMockCustomUser;

@ActiveProfiles("test")
@Import(TestSecurityConfiguration.class)
@WebMvcTest(UserClusterController.class) 
public class UserClusterControllerTest {
    @Autowired MockMvc mockMvc;

    @MockitoBean ClusterUserService clusterUserService;
    @MockitoBean ClusterProfileManageService clusterProfileManageService;

    @WithMockCustomUser
    @Test 
    void shouldNotThrowExceptionOnGetAvailable() throws Exception{
        when(clusterUserService.getAvailableClusters(anyLong())).thenReturn(List.of());
        mockMvc
            .perform(get("/api/cluster"))
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @WithMockCustomUser
    @Test 
    void shouldNotThrowExceptionOnGetAvailableBinded() throws Exception{
        when(clusterUserService.getBindedClusters(anyLong(), anyLong())).thenReturn(List.of());
        mockMvc
            .perform(get(
                "/api/cluster/{clusterId}/bound", 
                    ThreadLocalRandom.current().nextLong(1L, 999_999L)
            )).andExpect(status().is2xxSuccessful());
    }

    @WithMockCustomUser
    @Test 
    void shouldNotThrowExceptionOnGetTres() throws Exception{
        when(clusterUserService.getTres(anyLong())).thenReturn(List.of());
        mockMvc
            .perform(get(
                "/api/cluster/{clusterId}/tres", 
                    ThreadLocalRandom.current().nextLong(1L, 999_999L)
            )).andExpect(status().is2xxSuccessful());
    }

    @WithMockCustomUser
    @Test 
    void shouldNotThrowExceptionOnGetProfile() throws Exception{
        when(clusterProfileManageService.getUserProfile(anyLong(), anyLong(), anyString()))
            .thenReturn(ClusterProfileResponse.builder().build());
        mockMvc
            .perform(get(
                "/api/cluster/{clusterId}/bound/{clusterName}/profile", 
                    ThreadLocalRandom.current().nextLong(1L, 999_999L),
                    "TestCluster"
            )).andExpect(status().is2xxSuccessful());
    }

    @WithMockCustomUser
    @Test 
    void shouldNotThrowExceptionOnDownloadProfile() throws Exception{
        when(clusterProfileManageService.downloadProfiles(anyLong(), anyString(), anySet()))
            .thenReturn(new ByteArrayResource("testResource".getBytes(), "test"){
                @Override
                @Nullable
                public String getFilename() {
                    return "testresource";
                }
            });

        mockMvc
            .perform(get(
                "/api/cluster/{clusterId}/bound/{clusterName}/profile/download", 
                    ThreadLocalRandom.current().nextLong(1L, 999_999L),
                    "TestCluster"
            ))
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }
}
