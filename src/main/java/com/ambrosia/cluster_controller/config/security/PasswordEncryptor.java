package com.ambrosia.cluster_controller.config.security;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordEncryptor implements PasswordEncoder{
    private final TextEncryptor textEncryptor;

    @Override
    public String encode(CharSequence rawPassword) {
        return textEncryptor.encrypt(rawPassword.toString());
    }
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            String decrypted = textEncryptor.decrypt(encodedPassword);
            return decrypted.equals(rawPassword.toString());
        } catch (Exception e) {
            return false;
        }
    }   
    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return PasswordEncoder.super.upgradeEncoding(encodedPassword);
    }

    public String decode(String encodedPassword){
        try {
            return textEncryptor.decrypt(encodedPassword);
        } catch (Exception e) {
            return null;
        }
    }
}
