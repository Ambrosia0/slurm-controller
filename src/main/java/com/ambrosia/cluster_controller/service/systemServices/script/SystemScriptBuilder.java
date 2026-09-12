package com.ambrosia.cluster_controller.service.systemServices.script;

import java.util.List;
import java.util.stream.Collectors;

import com.ambrosia.cluster_controller.model.DTO.scheduler.Association;

import jakarta.annotation.Nullable;

/**
 * Creates scripts for managing OS users
 * SystemScriptBuilder
 */
public interface SystemScriptBuilder {
    String buildProfileCreationScript(List<Association> clusterProfile);

    @Nullable String buildProfileUpdateScript(List<Association> clusterProfile);
    
    default String buildProfileDeletionScript(List<String> usernames){
        return usernames.stream()
            .map(t -> String.format("sudo userdel -r -f %s", t))
            .collect(Collectors.joining("\n"));
    };

    default String buildUserExistanceCheckScript(String username){
        return String.format("id %s", username.toLowerCase());
    }

    default String buildUserIdReceiveScript(String username){
        return String.format("id -u %s", username.toLowerCase());
    }
}
