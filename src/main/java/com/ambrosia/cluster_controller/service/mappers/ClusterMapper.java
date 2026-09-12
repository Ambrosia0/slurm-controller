package com.ambrosia.cluster_controller.service.mappers;

import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.config.security.PasswordEncryptor;
import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.ClusterAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.user.response.ClusterUserResponse;
import com.ambrosia.cluster_controller.model.entity.Cluster;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ClusterMapper {
    private final PasswordEncryptor passwordEncryptor;

    public Cluster toEntity(ClusterAdminRequest dto){
        var cluster = Cluster.builder()
            .host(dto.host());

        if(dto.password() != null && !dto.password().isEmpty()){
            cluster.password(passwordEncryptor.encode(dto.password()));
        }else{
            cluster.password(null);
        }
        cluster.schema(dto.schema());
        cluster.scheduler(dto.taskScheduler());
        cluster.displayedName(dto.displayedName());

        if(dto.daemonPort() != null){
            cluster.daemonPort(dto.daemonPort());
        }

        if (dto.sshPort() != null){
            cluster.sshPort(dto.sshPort());
        }
        cluster.username(dto.username());
        return cluster.build();
    }
    

    public ClusterAdminResponse toAdminResponse(Cluster cluster){
        var dto = new ClusterAdminResponse(
            cluster.getId(),
            cluster.getHost(),
            cluster.getUsername(),
            cluster.getDisplayedName(),
            cluster.getDaemonPort(),
            cluster.getSshPort(),
            cluster.getScheduler()
        );
        return dto;
    }

    public ClusterUserResponse toUserResponse(Cluster cluster){
        var dto = new ClusterUserResponse(
            cluster.getId(),
            cluster.getHost(),
            cluster.getDisplayedName());
        return dto;
    }
}
