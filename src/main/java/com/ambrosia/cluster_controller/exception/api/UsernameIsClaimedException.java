package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class UsernameIsClaimedException extends ApiException{
    public UsernameIsClaimedException(){
        super(HttpStatus.CONFLICT, "Username is already claimed!");
    }
}
