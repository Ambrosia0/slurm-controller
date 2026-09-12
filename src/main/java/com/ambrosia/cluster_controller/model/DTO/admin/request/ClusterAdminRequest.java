package com.ambrosia.cluster_controller.model.DTO.admin.request;

import com.ambrosia.cluster_controller.util.HttpSchema;
import com.ambrosia.cluster_controller.util.SupportedTaskSchedulers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record ClusterAdminRequest(
    @NotBlank
    @Pattern(
        regexp = "[a-zA-Z0-9.-]+$",
        message = "Invalid host"
    )
    String host,

    @NotNull
    @Pattern(
        regexp = "^[a-z][-a-z0-9]{0,31}$",
        message = "Username is not resolved"
    )
    String username,

    String password,

    @NotNull
    String displayedName,

    @Positive 
    @Max(65535)
    Integer daemonPort,

    @NotNull
    HttpSchema schema,

    @Positive 
    @Max(65535)
    Integer sshPort,

    @NotNull
    SupportedTaskSchedulers taskScheduler
) {
    public ClusterAdminRequest{
        if(sshPort == null)
            sshPort = 22;
        
        if(daemonPort == null)
            daemonPort = 6820;
    }
}
