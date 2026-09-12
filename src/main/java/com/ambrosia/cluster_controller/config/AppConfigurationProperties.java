package com.ambrosia.cluster_controller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
@Component
@ConfigurationProperties(prefix = "app.default")
public class AppConfigurationProperties {
    private String passwordEncryptionKey;
    private String passwordEncryptionSalt;
    private String adminUsername;
    private String adminPassword;
    private String userGroup = "webguiusers";
    private String account = "default";
    private long slurmTokenDuration;
    private String diskQuotaPath="/date";
    private String homeDirectoryPath="/home";
    private long accessTokenDuration = 18000;
    private long refreshTokenDuration = 36000;
    private Boolean quotaSupported = false;
}
