package com.ambrosia.cluster_controller.service.databaseHandlers.impl;

import com.ambrosia.cluster_controller.config.security.PasswordEncryptor;
import com.ambrosia.cluster_controller.exception.api.GroupDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.UserDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.UsernameIsClaimedException;
import com.ambrosia.cluster_controller.model.DTO.admin.request.UserAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.UserAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.UserFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.UserImport;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.repository.GroupRepository;
import com.ambrosia.cluster_controller.repository.UserRepository;
import com.ambrosia.cluster_controller.repository.specification.UserSpecification;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileRemover;
import com.ambrosia.cluster_controller.service.databaseHandlers.UserManageService;
import com.ambrosia.cluster_controller.service.mappers.UserMapper;
import com.ambrosia.cluster_controller.util.Role;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserManageServiceImpl implements UserManageService {
    private final UserRepository userRepository;

    private final PasswordEncryptor passwordEncryptor;

    private final GroupRepository groupRepository;

    private final ObjectMapper objectMapper;
    
    private final UserMapper userMapper;

    private final Validator validator;

    private final ClusterProfileRemover clusterProfileRemover;


    @Override
    public UserAdminResponse createUser(UserAdminRequest dto) {
        if (dto.groupId() != null && !groupRepository.existsById(dto.groupId())) {
            throw new GroupDoesntExistException();
        }
        if (userRepository.existsByUsername(dto.username())) {
            throw new UsernameIsClaimedException();
        }
        return userMapper.toAdminResponse(
            userRepository.save(userMapper.toEntity(dto))
        );
    }

    @Transactional 
    @Override
    public UserAdminResponse updateUser(long userId, UserAdminRequest userDTO) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new UserDoesntExistException(userId));
        if(user.getRole() == Role.ROLE_ADMIN){
            throw new UserDoesntExistException(userId);
        }
        if(userRepository.existsByUsername(userDTO.username())){
            throw new UsernameIsClaimedException();
        }
        user.setUsername(userDTO.username());
        user.setPassword(
            passwordEncryptor.encode(
                userDTO.password()
            )
        );
        return userMapper.toAdminResponse(
            userRepository.save(user)
        );
    }

    @Transactional
    @Override
    public void deleteById(long userId) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new UserDoesntExistException());
        if(user.getRole() == Role.ROLE_ADMIN){
            throw new UserDoesntExistException(); 
        }
        clusterProfileRemover.removeProfilesByUserId(userId);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    @Override
    public void exportUsers(HttpServletResponse response) {
        try {
            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment;filename=users.json");
            JsonGenerator generator = objectMapper.getFactory().createGenerator(response.getOutputStream());
            generator.setPrettyPrinter(new DefaultPrettyPrinter());
            
            generator.writeStartArray();
            try(Stream<User> stream = userRepository.streamAll()){
                stream.forEach(user ->{
                    try {
                        generator.writeObject(userMapper.toAdminResponse(user));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            generator.writeEndArray();
            generator.flush();
            generator.close();
        } catch (IOException e) {
            log.error("Exception caught while exporting users! {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't export!");
        }
    }

    @Transactional
    @Override
    public void importUsers(MultipartFile file) {
        try(InputStream is = file.getInputStream();
            JsonParser parser = new JsonFactory().createParser(is)){
            if(parser.nextToken() != JsonToken.START_ARRAY){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Json array expected");
            }
            while(parser.nextToken() != JsonToken.END_ARRAY){
                
                UserImport dto = objectMapper.readValue(parser, UserImport.class);
                Set<ConstraintViolation<UserImport>> violations = validator.validate(dto);
                
                if(!violations.isEmpty()){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user-data");
                }
                User user = userMapper.toSave(dto);
                if (user.getGroup() != null &&
                    !groupRepository.existsByName(user.getGroup().getName())) {
                    user.setGroup(groupRepository.saveAndFlush(user.getGroup()));
                } else if(user.getGroup() != null){
                    user.setGroup(groupRepository.findByName(user.getGroup().getName()).get());
                }
                userRepository.save(user);
            }
        } catch(IOException ex){
            log.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Can't import");
        }
    }

    @Override
    public Page<UserAdminResponse> searchUsers(UserFilter userFilter, Pageable pageable) {
        var spec = Specification.<User>unrestricted()
            .and(UserSpecification.user())
            .and(UserSpecification.usernameContains(userFilter.username()))
            .and(UserSpecification.containsProfileInCluster(userFilter.clusterId(), userFilter.bindedCluster(), userFilter.notInCluster()))
            .and(userFilter.ungrouped()?
                UserSpecification.doesntContainGroup():
                userFilter.groupId() != null?
                    UserSpecification.groupContains(userFilter.groupId()):
                    null
            );
        return userRepository.findAll(spec, pageable)
            .map(userMapper::toAdminResponse);
    }
}
