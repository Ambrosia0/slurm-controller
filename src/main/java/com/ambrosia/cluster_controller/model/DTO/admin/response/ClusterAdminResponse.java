package com.ambrosia.cluster_controller.model.DTO.admin.response;

import com.ambrosia.cluster_controller.util.SupportedTaskSchedulers;

public record ClusterAdminResponse(
    long id,
    String hostname,
    String username,
    String displayedName,
    int daemonPort,
    int sshPort,
    SupportedTaskSchedulers taskScheduler
) {}
