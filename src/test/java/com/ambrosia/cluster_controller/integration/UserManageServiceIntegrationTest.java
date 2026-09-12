package com.ambrosia.cluster_controller.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import com.ambrosia.cluster_controller.BaseIntegrationTest;
import com.ambrosia.cluster_controller.exception.api.GroupDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.UserDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.UsernameIsClaimedException;
import com.ambrosia.cluster_controller.model.DTO.admin.request.UserAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.generic.UserImport;
import com.ambrosia.cluster_controller.repository.UserRepository;
import com.ambrosia.cluster_controller.service.databaseHandlers.UserManageService;
import com.ambrosia.cluster_controller.util.Role;
import com.ambrosia.cluster_controller.util.creators.UserCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ambrosia.cluster_controller.model.DTO.filters.UserFilter;

@Import(UserCreator.class)
public class UserManageServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired UserManageService userService;

    @Autowired ObjectMapper objectMapper;

    @Autowired UserCreator userCreator;

    @Autowired UserRepository userRepository;

    @AfterEach
    void cleanUp(){
        userCreator.deleteAll();
    }

    @Test
    void shouldThrowGroupDoesntExistExceptionOnCreate(){
        assertThrows(
            GroupDoesntExistException.class,
            () -> 
                userService.createUser(
                    createRequest(
                        randUsername(),
                        ThreadLocalRandom.current().nextLong()
                    )
                )
        );
    }

    @Test
    void shouldThrowUsernameIsClaimedExceptionOnCreate(){
        var user = userCreator.createUser(Role.ROLE_USER);
        assertThrows(
            UsernameIsClaimedException.class,
            () -> userService.createUser(createRequest(user.getUsername(), null))
        );
    }

    @Test
    void shouldCreateUser(){
        var username = randUsername();
        assertDoesNotThrow(
            () -> userService.createUser(
                createRequest(
                    username,
                    null
                )
            )
        );
        assertTrue(userRepository.existsByUsername(username));
    }

    @Test
    void shouldThrowUserDoesntExistExceptionOnUpdate(){
        assertThrows(
            UserDoesntExistException.class,
            () -> userService.updateUser(
                randLong(),
                createRequest(randUsername(), null)
            )
        );
    }
    
    @Test
    void shouldThrowUsernameIsClaimedExceptionOnUpdate(){
        var user = userCreator.createUser(Role.ROLE_USER);
        assertThrows(
            UsernameIsClaimedException.class,
            () -> userService.updateUser(
                user.getId(),
                createRequest(user.getUsername(), null)
            )
        );
    }

    @Test
    void shouldUpdateUser(){
        var user = userCreator.createUser(Role.ROLE_USER);
        var username = randUsername();
        assertDoesNotThrow(() -> userService.updateUser(
            user.getId(), 
            createRequest(username, null)
        ));
        assertTrue(userRepository.existsByUsername(username));
    }

    @Test
    void shouldThrowUserDoesntExistOnDelete(){
        assertThrows(
            UserDoesntExistException.class,
            () -> userService.deleteById(randLong())
        );
    }

    @Test
    void shouldThrowUserDoesntExistOnDeleteAdmin(){
        var user = userCreator.createUser(Role.ROLE_ADMIN);
        assertThrows(
            UserDoesntExistException.class,
            () -> userService.deleteById(user.getId())
        );
    }

    @Test
    void shouldDeleteUser(){
        var user = userCreator.createUser(Role.ROLE_USER);
        assertDoesNotThrow(() -> userService.deleteById(user.getId()));
        assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    void shouldReturnUsers(){
        userCreator.createUser(Role.ROLE_USER);
        assertNotEquals(
            0, 
            userService.searchUsers(UserFilter.builder().build(), PageRequest.of(0, 10))
                .getContent().size()
        );
    }

    @Test
    void shouldExportUsers() throws Exception{
        userCreator.createUser(Role.ROLE_USER);
        userCreator.createUser(Role.ROLE_USER);
        userCreator.createUser(Role.ROLE_USER);
        var resp = new MockHttpServletResponse();
        assertDoesNotThrow(() -> userService.exportUsers(resp));
        assertNotEquals(0, resp.getContentAsString().length());
    }

    @Test
    void shouldImportUsers() throws Exception{
        var userToImport = List.of(createImport());
        var file = new MockMultipartFile(
            "users",
            objectMapper.writeValueAsBytes(userToImport)
        );
        assertDoesNotThrow(() -> userService.importUsers(file));
        assertTrue(userRepository.existsByUsername(userToImport.getFirst().username()));
    }

    @Test
    void shouldSearchUser(){
        var user = userCreator.createUser(Role.ROLE_USER);
        var filter = UserFilter.builder()
            .username(user.getUsername().substring(0, 3))
            .build();
        assertEquals(1, 
            userService.searchUsers(
                filter,
                PageRequest.of(0, 10)
            ).getContent().size()
        );
    }

    private UserImport createImport(){
        return new UserImport(
            randUsername(), 
            "testpassworddddd" +Long.toString(randLong()),
            null
        );
    }

    private Long randLong(){
        return ThreadLocalRandom.current().nextLong(1L, 999_999_999L);
    }   

    private String randUsername(){
        return "TestUser"+ThreadLocalRandom.current().nextLong(1L, 999_999_999L);
    }

    private UserAdminRequest createRequest(String username, Long groupId){
        return new UserAdminRequest(
            username,
            "testpasswordssss0",
            groupId
        );
    }
}
