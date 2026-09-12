package com.ambrosia.cluster_controller.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        var user = userRepository
            .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        return new CustomUserDetails(
            user.getId(), 
            user.getUsername(), 
            user.getPassword(), 
            true,
            user.getRole().name()
        );
    }
}
