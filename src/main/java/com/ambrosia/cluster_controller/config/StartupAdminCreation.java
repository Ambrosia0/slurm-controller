package com.ambrosia.cluster_controller.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.config.security.PasswordEncryptor;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.repository.UserRepository;
import com.ambrosia.cluster_controller.util.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Component for startup admin initialization
 * StartupAdminCreation
 */
@Slf4j 
@Component
@RequiredArgsConstructor  
public class StartupAdminCreation implements ApplicationRunner{
    private final UserRepository userRepository;

    private final AppConfigurationProperties appConfigurationProperties;

    private final PasswordEncryptor passwordEncryptor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var admin = userRepository.findByUsername(appConfigurationProperties.getAdminUsername());
        if(admin.isEmpty()){
            log.info("Intializing root admin!");
            var user = User.builder()
                .role(Role.ROLE_ADMIN)
                .username(appConfigurationProperties.getAdminUsername())
                .password(passwordEncryptor.encode(appConfigurationProperties.getAdminPassword()))
                .build();
            userRepository.saveAndFlush(user);
            log.info("Root admin created!");
        }else{
            log.info("Root admin already initialized!");
        }
    }
}
