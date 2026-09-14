package com.ambrosia.cluster_controller.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.ambrosia.cluster_controller.controller.api.admin.AdminGroupController;
import com.ambrosia.cluster_controller.model.DTO.admin.request.GroupAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.filters.GroupFilter;
import com.ambrosia.cluster_controller.service.databaseHandlers.GroupManageService;
import com.ambrosia.cluster_controller.util.TestSecurityConfiguration;
import com.ambrosia.cluster_controller.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@Import(TestSecurityConfiguration.class)
@WebMvcTest(AdminGroupController.class)
public class AdminGroupControllerTest {
    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @MockitoBean GroupManageService groupManageService;

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnCreateGroup() throws Exception{
        mockMvc.perform(post("/api/group")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new GroupAdminRequest("testName")
            ))
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnGroupUsers() throws Exception{
        mockMvc.perform(post("/api/group/{groupId}/users", TestUtils.randLong())
            .param("userIds", TestUtils.randLong().toString(), TestUtils.randLong().toString())
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnUngroupUsers() throws Exception{
        mockMvc.perform(delete("/api/group/{groupId}/users", TestUtils.randLong())
            .param("userIds", TestUtils.randLong().toString(), TestUtils.randLong().toString())
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnGetGroups() throws Exception{
        when(groupManageService.getGroups(any(GroupFilter.class), any(Pageable.class)))
            .thenReturn(Page.empty());
        mockMvc.perform(get("/api/group")).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnExportGroup() throws Exception{
        mockMvc.perform(
            get("/api/group/{groupId}/export", TestUtils.randLong())
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnUpdateGroup() throws Exception{
        mockMvc.perform(patch("/api/group/{groupId}", TestUtils.randLong())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new GroupAdminRequest("TestGroup")))
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnDeleteGroup() throws Exception{
        mockMvc.perform(delete("/api/group/{groupId}", TestUtils.randLong())
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is2xxSuccessful());
    }

    @WithMockUser
    @Test 
    void shouldReturnForbiddenStatusOnDeleteGroupForUser() throws Exception{
        mockMvc.perform(delete("/api/group/{groupId}", TestUtils.randLong())
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @WithMockUser
    @Test 
    void shouldReturnForbiddenStatusOnDeleteGroupForAnonymous() throws Exception{
        mockMvc.perform(delete("/api/group/{groupId}", TestUtils.randLong())
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }
}
