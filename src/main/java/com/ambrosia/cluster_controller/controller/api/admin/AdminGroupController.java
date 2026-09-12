package com.ambrosia.cluster_controller.controller.api.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.cluster_controller.model.DTO.admin.request.GroupAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.GroupAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.GroupFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.GroupUsersRequest;
import com.ambrosia.cluster_controller.service.databaseHandlers.GroupManageService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminGroupController {
    private final GroupManageService groupService;
    
    @PostMapping
    public void createGroup(
            @RequestBody @Validated GroupAdminRequest dto) {
        groupService.createGroup(dto.name());
    }

    @PostMapping("/{groupId}/users")
    public void groupUsers(
            @PathVariable Long groupId,
            @RequestBody @Validated GroupUsersRequest request) {
        groupService.groupUsers(groupId, request);
    }

    @DeleteMapping("/{groupId}/users")
    public void ungroupUsers(
            @RequestBody Set<Long> userIds) {
        groupService.ungroupUsers(userIds);
    }

    @GetMapping
    public Page<GroupAdminResponse> getGroups(
            @ModelAttribute GroupFilter groupFilter,
            @PageableDefault(page = 0, size = 20) Pageable pageable
    ) {
        return groupService.getGroups(groupFilter, pageable);
    }

    @GetMapping("/{groupId}/export")
    public void exportGroup(
            @PathVariable Long groupId,
            HttpServletResponse response) { 
        groupService.exportGroup(groupId, response);
    }

    @PatchMapping("/{groupId}")
    public GroupAdminResponse update(
            @PathVariable Long groupId,
            @RequestBody @Validated GroupAdminRequest request) {
        return groupService.updateGroup(groupId, request);
    }

    @DeleteMapping("/{groupId}")
    public void delete(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "false") boolean deleteBinded){
        groupService.deleteGroup(groupId, deleteBinded);
    }
}
