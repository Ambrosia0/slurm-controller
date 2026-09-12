package com.ambrosia.cluster_controller.taskSchedulers.slurm.impl;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.config.AppConfigurationProperties;
import com.ambrosia.cluster_controller.model.DTO.scheduler.ProfileTask;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.service.systemServices.SshCommandSender;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTokenRequester;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmUserTokenData;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SlurmSshTokenRequester implements SlurmTokenRequester{
    private final SshCommandSender sender;

    private final AppConfigurationProperties appConfigurationProperties;

    @Override
    public @Nullable SlurmUserTokenData refreshToken(Cluster cluster) {
        try {
            var token = sender.execute(
                cluster.getHost(),
                cluster.getUsername().toLowerCase(),
                cluster.getPassword(),
                cluster.getSshPort(),
                "unset SLURM_JWT; export $(scontrol token); echo \"$SLURM_JWT\"", true);
            var tokenData = new SlurmUserTokenData(
                    token.replaceAll("\\s+", ""), 
                    Instant.now().plusSeconds(appConfigurationProperties.getSlurmTokenDuration()));
            return tokenData;
        } catch (Exception e) {
            log.error(
                "Can't execute command on cluster {}:{}!",
                cluster.getHost(),
                cluster.getUsername(), 
                e
            );
            return null;
        }
    }

    @Override
    public @Nullable SlurmUserTokenData requestUserToken(Cluster cluster, ProfileTask profile) {
        try {
            String token = sender.executeOnce(
                cluster.getHost(), 
                profile.username().toLowerCase(), 
                profile.password(), 
                cluster.getSshPort(), 
                "unset SLURM_JWT; export $(scontrol token); echo \"$SLURM_JWT\"");
            
            var tokenData = new SlurmUserTokenData(
                token.replaceAll("\\s+", ""), 
                Instant.now().plusSeconds(appConfigurationProperties.getSlurmTokenDuration()));
            // userTokens.put(profile.getId().getUser().getId(),tokenData);
            return tokenData;
        } catch (Exception e) {
            log.error(
                "Can't execute command on cluster {}:{} to get token for user {}! ", 
                cluster.getHost(),
                cluster.getUsername(),
                profile.username(),
                e
            );
            return null;
        }
    }
}
