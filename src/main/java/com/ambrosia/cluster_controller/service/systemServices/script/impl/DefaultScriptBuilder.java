package com.ambrosia.cluster_controller.service.systemServices.script.impl;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.config.AppConfigurationProperties;
import com.ambrosia.cluster_controller.model.DTO.scheduler.Association;
import com.ambrosia.cluster_controller.service.systemServices.script.SystemScriptBuilder;

import jakarta.annotation.Nullable;

@Component
public class DefaultScriptBuilder implements SystemScriptBuilder{
    private AppConfigurationProperties appConfigurationProperties;

    private String CREATION_SCRIPT = """
    bash -c '
        if id -u %s >/dev/null 2>&1;
            then echo ERROR_ALREADY_EXISTS;
            exit 2;
        else (
            sudo useradd -m -d %s/%s %s && 
            echo %s | base64 -d | sudo chpasswd && 
            sudo chown %s:%s %s/%s &&
            sudo chmod 770 %s/%s &&
            %s
            id -u %s
        ) || { 
            echo ERROR_UID_NOT_PARSED; 
            sudo userdel -r -f %s; 
            exit 1; 
        }; 
        fi'
    """;

    private String QUOTA_UPDATE_SCRIPT = "setquota -u %s %s %s 0 0 %s &&";

    private String QUOTA_SCRIPT = "sudo setquota -u %s %s %s 0 0 %s &&";

    public DefaultScriptBuilder(
            AppConfigurationProperties appConfigurationProperties
    ){
        this.appConfigurationProperties = appConfigurationProperties;
    }

    @Override
    public String buildProfileCreationScript(List<Association> clusterProfile) {
        return clusterProfile.stream()
            .map(this::buildCreate)
            .collect(Collectors.joining("\n"));
    }



    @Override
    public @Nullable String buildProfileUpdateScript(List<Association> clusterProfile) {
        return appConfigurationProperties.getQuotaSupported()?
            clusterProfile.stream()
                .map(t -> {
                    return String.format(
                            QUOTA_UPDATE_SCRIPT, 
                            t.username().toLowerCase(),
                            Long.toString(t.softLimit())+"M",
                            Long.toString(t.hardLimit())+"M",
                            appConfigurationProperties.getDiskQuotaPath()
                        );
                })
                .collect(Collectors.joining("\n")):
            null;
    }

    private String buildCreate(Association profileCreate){
        var username = profileCreate.username();
        var quota = appConfigurationProperties.getQuotaSupported()?
            String.format(
                QUOTA_SCRIPT, 
                username,
                Long.toString(profileCreate.softLimit())+"M",
                Long.toString(profileCreate.hardLimit())+"M",
                appConfigurationProperties.getDiskQuotaPath()
            ):
            "";
        return String.format(
                CREATION_SCRIPT,
                username,
                appConfigurationProperties.getHomeDirectoryPath(),
                username,
                username,
                Base64.getEncoder().encodeToString(
                        (username+":"+profileCreate.password()).getBytes()
                    ),
                username,
                appConfigurationProperties.getUserGroup(),
                appConfigurationProperties.getHomeDirectoryPath(),
                username,
                appConfigurationProperties.getHomeDirectoryPath(),
                username,
                quota,
                username,
                username
            );
    }
}
