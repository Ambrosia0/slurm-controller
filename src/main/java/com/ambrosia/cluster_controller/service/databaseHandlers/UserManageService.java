package com.ambrosia.cluster_controller.service.databaseHandlers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.ambrosia.cluster_controller.model.DTO.admin.request.UserAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.UserAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.UserFilter;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Service for managing application users
 * UserService
 */
public interface UserManageService {
    /**
     * Creates user
     * @param request DTO with user information
     * @return Created user
     */
    UserAdminResponse createUser(UserAdminRequest request);

    /**
     * Updates user information
     * @param id ID of user to update 
     * @param request DTO with user information
     * @return Updated user
     */
    UserAdminResponse updateUser(long id, UserAdminRequest request);

    /**
     * Deletes user by ID
     * @param id ID of the user
     */
    void deleteById(long id);
    
    /**
     * Exports users to JSON
     * @param response
     */
    void exportUsers(HttpServletResponse response);

    /**
     * Imports users from JSON
     * @param file
     */
    void importUsers(MultipartFile file);

    /**
     * Search users
     * @param username
     * @param pageable
     * @param userFilter filter
     * @return
     */
    Page<UserAdminResponse> searchUsers(UserFilter userFilter, Pageable pageable);
}
