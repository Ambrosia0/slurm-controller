package com.ambrosia.cluster_controller.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ambrosia.cluster_controller.controller.api.admin.AdminProfileController;
import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterProfileAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.filters.ClusterProfileFilter;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileManageService;
import com.ambrosia.cluster_controller.util.TestSecurityConfiguration;
import com.ambrosia.cluster_controller.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@Import(TestSecurityConfiguration.class)
@WebMvcTest(AdminProfileController.class) 
public class AdminProfileControllerTest {
    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;
    
    @MockitoBean ClusterProfileManageService clusterProfileManageService;
    

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnCreateProfile() throws Exception{
        mockMvc.perform(post(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/profile",
            TestUtils.randLong(),
            TestUtils.randName()
            )
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(
                ClusterProfileAdminRequest.builder()
                    .userId(TestUtils.randLong())
                    .build() 
            )))
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnUpdateProfile() throws Exception{
        mockMvc.perform(put(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/profile",
            TestUtils.randLong(),
            TestUtils.randName()
            )
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(
                ClusterProfileAdminRequest.builder()
                    .userId(TestUtils.randLong())
                    .build() 
            )))
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnDownloadProfile() throws Exception{
        when(clusterProfileManageService.downloadProfiles(anyLong(), anyString(), anySet()))
            .thenReturn(new ByteArrayResource("test".getBytes()){
                @Override
                @Nullable
                public String getFilename() {
                    return "testfile";
                }
            });
        mockMvc.perform(get(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/download",
            TestUtils.randLong(),
            TestUtils.randName()
            )
            .param("userIds", TestUtils.randLong().toString(), TestUtils.randLong().toString())
            .content(objectMapper.writeValueAsString(List.of(
                ClusterProfileAdminRequest.builder()
                    .userId(TestUtils.randLong())
                    .build() 
            )))
            .accept(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnDeleteProfile() throws Exception{
        mockMvc.perform(delete(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/profile/{userId}",
                TestUtils.randLong(),
                TestUtils.randName(),
                TestUtils.randLong()
            )
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnDeleteProfiles() throws Exception{
        mockMvc.perform(delete(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/profile",
                TestUtils.randLong(),
                TestUtils.randName()
            )
            .param("userIds", TestUtils.randLong().toString(), TestUtils.randLong().toString())
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnGetProfiles() throws Exception{
        when(clusterProfileManageService.search(any(ClusterProfileFilter.class), any(Pageable.class)))
            .thenReturn(Page.empty());
        mockMvc.perform(get("/api/admin/cluster/profile")
            .param("userIds", TestUtils.randLong().toString(), TestUtils.randLong().toString())
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser
    @Test 
    void shouldReturnFobiddenStatusOnDeleteProfilesForUser() throws Exception{
        mockMvc.perform(delete(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/profile",
                TestUtils.randLong(),
                TestUtils.randName()
            )
            .param("userIds", TestUtils.randLong().toString(), TestUtils.randLong().toString())
        ).andExpect(status().isForbidden());
    }

    @WithAnonymousUser 
    @Test 
    void shouldReturnFobiddenStatusOnDeleteProfilesForAnonymous() throws Exception{
        mockMvc.perform(delete(
            "/api/admin/cluster/{clusterId}/bound/{clusterName}/profile",
                TestUtils.randLong(),
                TestUtils.randName()
            )
            .param("userIds", TestUtils.randLong().toString(), TestUtils.randLong().toString())
        ).andExpect(status().isForbidden());
    }
}
