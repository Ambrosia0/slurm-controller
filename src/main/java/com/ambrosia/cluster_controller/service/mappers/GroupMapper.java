package com.ambrosia.cluster_controller.service.mappers;

import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.model.DTO.admin.request.GroupAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.GroupAdminResponse;
import com.ambrosia.cluster_controller.model.entity.Group;

@Component
public class GroupMapper {
    public Group toEntity(GroupAdminRequest dto){
        var group = new Group();
        group.setName(dto.name());
        return group;
    }

    public GroupAdminResponse toResponse(Group group){
        var dto = new GroupAdminResponse(
            group.getId(), 
            group.getName(), 
            group.getCreatedAt());
        return dto;
    }

}
