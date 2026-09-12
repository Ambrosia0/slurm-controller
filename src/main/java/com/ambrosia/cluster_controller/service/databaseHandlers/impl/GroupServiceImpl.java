package com.ambrosia.cluster_controller.service.databaseHandlers.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ambrosia.cluster_controller.exception.api.GroupDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.UserDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.UsernameIsClaimedException;
import com.ambrosia.cluster_controller.model.DTO.admin.request.GroupAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.GroupAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.admin.response.UserAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.GroupFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.GroupUsersRequest;
import com.ambrosia.cluster_controller.model.entity.Group;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.repository.GroupRepository;
import com.ambrosia.cluster_controller.repository.UserRepository;
import com.ambrosia.cluster_controller.repository.specification.GroupSpecification;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileRemover;
import com.ambrosia.cluster_controller.service.databaseHandlers.GroupManageService;
import com.ambrosia.cluster_controller.service.mappers.GroupMapper;
import com.ambrosia.cluster_controller.service.mappers.UserMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GroupServiceImpl implements GroupManageService {
    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private final UserMapper userMapper;

    private final GroupMapper groupMapper;

    private final ClusterProfileRemover clusterProfileRemover;

    @Override
    public Group createGroup(String name) {
        if(groupRepository.existsByName(name)){
            throw new UsernameIsClaimedException();
        }
        var group = new Group();
        group.setName(name);
        return groupRepository.save(group);
    }

    @Transactional
    @Override
    public void deleteGroup(long groupId, boolean deleteBinded) {
        var group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupDoesntExistException());
        if(deleteBinded){
            clusterProfileRemover.removeProfilesByGroupId(groupId);
            userRepository.deleteByGroup(group);
        }
        groupRepository.delete(group);
    }
    
    @Transactional 
    @Override
    public GroupAdminResponse updateGroup(long groupId, GroupAdminRequest dto) {
        var group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupDoesntExistException());
        group.setName(dto.name());
        group = groupRepository.saveAndFlush(group);
        return groupMapper.toResponse(group);
    }
    
    @Override
    public Page<GroupAdminResponse> getGroups(GroupFilter groupFilter, Pageable pageable) {
        var spec = Specification.<Group>unrestricted()
            .and(GroupSpecification.nameContains(groupFilter.name()))
            .and(GroupSpecification.clusterContains(
                    groupFilter.clusterId(), 
                    groupFilter.bindedCluster(), 
                    groupFilter.notInCluster())
            );
        return groupRepository.findAll(spec, pageable)
            .map(groupMapper::toResponse);
    }

    @Transactional 
    @Override
    public void groupUsers(long groupId, GroupUsersRequest request) {
        var group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupDoesntExistException());

        var ids = userRepository.findAdminIds();
        if(request.userIds().stream().anyMatch(ids::contains)){
            throw new UserDoesntExistException();
        }
        if(!userRepository.compareSize(request.userIds(), request.userIds().size())){
            throw new UserDoesntExistException();
        }
        userRepository.groupUsers(group, request.userIds());
    }

    @Transactional
    @Override
    public void ungroupUsers(Set<Long> userIds) {
        if(!userRepository.compareSize(userIds, userIds.size())){
            throw new UserDoesntExistException();
        }
        userRepository.ungroupUsers(userIds);
    }

    @Transactional(readOnly = true)
    @Override
    public void exportGroup(long groupId, HttpServletResponse response) {
        try {
            var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupDoesntExistException());
            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment;filename="+group.getName()+".json");
            JsonGenerator generator = objectMapper.getFactory().createGenerator(response.getOutputStream());
            generator.setPrettyPrinter(new DefaultPrettyPrinter());
            generator.writeStartArray();
            try(Stream<User> stream = userRepository.streamGroup(group)){
                stream.forEach(user ->{
                    try {
                        generator.writeObject(userMapper.toAdminResponse(user));
                    } catch (Exception e) {
                    }
                });
            }
            generator.writeEndArray();
            generator.flush();
            generator.close();
        } catch (IOException e) {
            log.warn("Exception caught while exporting grouped users! {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't export!");
        }
    }

    @Override
    public List<UserAdminResponse> getUnbindedInGroup(long clusterId, long groupId) {
        return userRepository.findUnbindedInGroup(clusterId, groupId).stream().map(userMapper::toAdminResponse).toList();
    }
}
