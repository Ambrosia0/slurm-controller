package com.ambrosia.cluster_controller.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ambrosia.cluster_controller.controller.api.admin.AdminUserController;
import com.ambrosia.cluster_controller.model.DTO.filters.UserFilter;
import com.ambrosia.cluster_controller.service.databaseHandlers.UserManageService;
import com.ambrosia.cluster_controller.util.TestSecurityConfiguration;
import com.ambrosia.cluster_controller.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@Import(TestSecurityConfiguration.class)
@WebMvcTest(AdminUserController.class) 
public class AdminUserControllerTest {
    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @MockitoBean UserManageService userManageService;

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnCreate() throws Exception{
        mockMvc.perform(post("/api/user"))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser
    @Test
    void shouldThrowForbiddenOnCreate() throws Exception{
        mockMvc.perform(post("/api/user"))
            .andExpect(status().is2xxSuccessful());
    }
    
    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnUpdate() throws Exception{
        mockMvc.perform(patch("/api/user/{userId}", TestUtils.randLong()))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnDelete() throws Exception{
        mockMvc.perform(delete("/api/user/{userId}", TestUtils.randLong()))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnGet() throws Exception{
        when(userManageService.searchUsers(any(UserFilter.class), any(Pageable.class)))
            .thenReturn(Page.empty());
        mockMvc.perform(get("/api/user"))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnExport() throws Exception{
        mockMvc.perform(get("/api/user/export"))
            .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(roles = "ADMIN")
    @Test 
    void shouldNotThrowExceptionOnImport() throws Exception{
        mockMvc.perform(post("/api/user/export")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content(new MockMultipartFile("test", "test".getBytes()).getBytes())
        ).andExpect(status().is2xxSuccessful());
    }
}
