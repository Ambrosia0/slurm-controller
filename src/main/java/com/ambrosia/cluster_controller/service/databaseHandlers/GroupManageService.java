package com.ambrosia.cluster_controller.service.databaseHandlers;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ambrosia.cluster_controller.model.DTO.admin.request.GroupAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.GroupAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.admin.response.UserAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.GroupFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.GroupUsersRequest;
import com.ambrosia.cluster_controller.model.entity.Group;

import jakarta.servlet.http.HttpServletResponse;
/**
 * Service for managing groups
 * GroupService
 */
public interface GroupManageService {
    /**
     * Creates group
     * @param name Name of the group to create
     * @return Created group
     */
    Group createGroup(String name);

    /**
     * Deletes group
     * @param groupId Group id to delete
     * @param deleteBinded if {@code true} deletes bound to group users, if {@code false} doesn't deletes bounded users
     */
    void deleteGroup(long groupId, boolean deleteBinded);

    /**
     * Updates group information
     * @param groupId Id of the group to update
     * @param request DTO containing information
     * @return Updated group
     */
    GroupAdminResponse updateGroup(long groupId, GroupAdminRequest request);

    /**
     * Groups multiple users
     * @param groupId Id of the group to use
     * @param request DTO containing user information
     */
    void groupUsers(long groupId, GroupUsersRequest request);

    /**
     * Ungroups users
     * @param userIds Ids of ungrouped users
     */
    void ungroupUsers(Set<Long> userIds);

    /**
     * Export group with users in JSON format
     * @param groupId Id of the group to export
     * @param response
     */
    void exportGroup(long groupId, HttpServletResponse response);

    Page<GroupAdminResponse> getGroups(GroupFilter groupFilter, Pageable pageable);

    /**
     * Get unbounded to cluster users in group
     * @param clusterId
     * @param groupId
     * @return Unbounded users
     */
    List<UserAdminResponse> getUnbindedInGroup(long clusterId, long groupId);
}
