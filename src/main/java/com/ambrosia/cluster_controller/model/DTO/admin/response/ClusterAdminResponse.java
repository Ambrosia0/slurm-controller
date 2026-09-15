package com.ambrosia.cluster_controller.model.DTO.admin.response;

import com.ambrosia.cluster_controller.util.SupportedTaskSchedulers;

import lombok.Builder;

@Builder 
public record ClusterAdminResponse(
    long id,
    String hostname,
    String username,
    String displayedName,
    int daemonPort,
    int sshPort,
    SupportedTaskSchedulers taskScheduler
) {}
