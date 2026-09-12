package com.ambrosia.cluster_controller.config.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    private long id;
    private String username;
    private String password;
    private boolean active;
    private GrantedAuthority authority;

    CustomUserDetails(long id, String username, String password, boolean active,
            String role){
        this.id = id;
        this.username = username;
        this.password = password;
        this.active = active;
        this.authority = new SimpleGrantedAuthority(role);
    }

    public long getId(){
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
