package com.ambrosia.cluster_controller.service.mappers;

import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.config.security.PasswordEncryptor;
import com.ambrosia.cluster_controller.model.DTO.admin.request.UserAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.UserAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.generic.UserImport;
import com.ambrosia.cluster_controller.model.entity.Group;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.util.Role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserMapper {
    private final PasswordEncryptor passwordEncryptor;

    private final GroupMapper groupMapper;

    public User toEntity(UserAdminRequest dto){
        var user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncryptor.encode(dto.password()));
        user.setRole(Role.ROLE_USER);
        if (dto.groupId() != null) {
            var group = new Group();
            group.setId(dto.groupId());
            user.setGroup(group);
        }
        return user;
    }

    public User toSave(UserImport dto){
        var user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncryptor.encode(dto.password()));
        user.setRole(Role.ROLE_USER);
        if (dto.group() != null && dto.group().getName() != null) {
            var group = new Group();
            group.setName(dto.group().getName());
            user.setGroup(group);
        }
        return user;
    }

    public UserAdminResponse toAdminResponse(User user){
        try {
            var dto = new UserAdminResponse(
                user.getId(),
                user.getUsername(),
                passwordEncryptor.decode(user.getPassword()), 
                user.getGroup()==null? null: groupMapper.toResponse(user.getGroup()),
                user.getCreatedAt());
                return dto;
        } catch (Exception e) {
            return null;
        }
        
    }
}
