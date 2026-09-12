package com.ambrosia.cluster_controller.util.creators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.ambrosia.cluster_controller.model.entity.Group;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.repository.UserRepository;
import com.ambrosia.cluster_controller.util.Role;
import com.ambrosia.cluster_controller.util.factory.UserFactory;

@TestComponent 
public class UserCreator {
    @Autowired TextEncryptor textEncryptor;

    @Autowired UserRepository userRepository;

    public User createUser(Role role){
        return userRepository.saveAndFlush(UserFactory.create(role, null, textEncryptor));
    }

    public User createUser(Role role, Group group){
        return userRepository.saveAndFlush(UserFactory.create(role, group, textEncryptor));
    }

    public void deleteAll(){
        userRepository.deleteAll();
    }
}
