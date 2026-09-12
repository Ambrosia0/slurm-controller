package com.ambrosia.cluster_controller.util.factory;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.ambrosia.cluster_controller.model.entity.Group;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.util.Role;

import jakarta.annotation.Nullable;

public class UserFactory {
    public static User create(Role role, @Nullable Group group, TextEncryptor textEncryptor){
        return User.builder()
            .username("TestUsername"+ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
            .password(textEncryptor.encrypt("testpassword"))
            .role(role)
            .group(group)
            .build();
    }
}
