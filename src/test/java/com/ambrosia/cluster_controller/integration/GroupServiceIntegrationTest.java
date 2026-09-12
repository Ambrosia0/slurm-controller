package com.ambrosia.cluster_controller.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.ambrosia.cluster_controller.BaseIntegrationTest;
import com.ambrosia.cluster_controller.exception.api.GroupDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.UsernameIsClaimedException;
import com.ambrosia.cluster_controller.model.DTO.admin.request.GroupAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.filters.GroupFilter;
import com.ambrosia.cluster_controller.model.entity.Group;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.repository.GroupRepository;
import com.ambrosia.cluster_controller.repository.UserRepository;
import com.ambrosia.cluster_controller.service.databaseHandlers.GroupManageService;
import com.ambrosia.cluster_controller.util.Role;
import com.ambrosia.cluster_controller.util.creators.UserCreator;
import com.ambrosia.cluster_controller.util.factory.GroupFactory;

@Import(UserCreator.class)
public class GroupServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired GroupRepository groupRepository;

    @Autowired GroupManageService groupService;

    @Autowired UserCreator userCreator;

    @Autowired UserRepository userRepository;

    @AfterAll
    void cleanUp(){
        groupRepository.deleteAll();
        userCreator.deleteAll();
    }

    @Test
    void shouldThrowUsernameIsClaimedException(){
        var group = createGroup();
        assertThrows(
            UsernameIsClaimedException.class,
            () -> groupService.createGroup(group.getName())    
        );
    }

    @Test
    void shouldCreateGroup(){
        var name = randUsername();
        assertDoesNotThrow(
            () -> groupService.createGroup(name)
        );
        assertTrue(groupRepository.existsByName(name));
    }

    @Test
    void shouldThrowGroupDoesntExistOnDelete(){
        assertThrows(
            GroupDoesntExistException.class,
            () -> groupService.deleteGroup(randLong(), false)
        );
    }

    @Test
    void shouldDeleteGroup(){
        var group = createGroup();
        assertDoesNotThrow(() -> groupService.deleteGroup(group.getId(), false));
        assertFalse(groupRepository.existsByName(group.getName()));
    }

    @Test
    void shouldDeleteRelatedUsers(){
        var users = createGroupWithUsers();
        assertDoesNotThrow(() -> groupService.deleteGroup(
            users.getGroup().getId(), 
            true)
        );
        assertFalse(userRepository.existsById(users.getId()));
    }

    @Test
    void shouldThrowGroupDoesntExistExceptionOnUpdate(){
        assertThrows(
            GroupDoesntExistException.class,
            () -> groupService.updateGroup(
                randLong(),
                new GroupAdminRequest(randUsername())
            )
        );
    }

    @Test
    void shouldUpdateGroupName(){
        var name = randUsername();
        var group = createGroup();
        assertDoesNotThrow(() -> groupService.updateGroup(
            group.getId(),
            new GroupAdminRequest(name))
        );
        assertTrue(groupRepository.existsByName(name));
    }

    @Test
    void shouldFindGroup(){
        var group = createGroup();
        var filter = GroupFilter.builder()
            .name(group.getName().substring(0, 3))
            .build();
        assertDoesNotThrow(
            () -> {
                groupService.getGroups(filter, PageRequest.of(0, 10)).getContent().getFirst();
            }
        );
    }


    @Test
    void shouldUngroupUsers(){
        var user = createGroupWithUsers();
        assertDoesNotThrow(() -> groupService.ungroupUsers(Set.of(user.getId())));
        assertNull(userRepository.findById(user.getId()).get().getGroup());
    }


    @Test
    void shouldExportGroup(){
        createGroupWithUsers();
        var user = createGroupWithUsers();
        var resp = new MockHttpServletResponse();
        assertDoesNotThrow(() -> groupService.exportGroup(user.getGroup().getId(), resp));
        assertNotNull(resp.getContentLength());

    }
    private Long randLong(){
        return ThreadLocalRandom.current().nextLong();
    }

    private String randUsername(){
        return "TestGroup"+ThreadLocalRandom.current().nextLong(1L, 999_999_999L);
    }

    private Group createGroup(){
        return groupRepository.saveAndFlush(GroupFactory.create());
    }

    private User createGroupWithUsers(){    
        var group = groupRepository.saveAndFlush(GroupFactory.create());
        var user = userCreator.createUser(Role.ROLE_USER, group);
        return user;
    }
}
