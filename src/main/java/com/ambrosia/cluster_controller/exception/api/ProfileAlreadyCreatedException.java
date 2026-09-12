package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class ProfileAlreadyCreatedException extends ApiException{
    public ProfileAlreadyCreatedException(){
        super(HttpStatus.CONFLICT, "Profile already created!");
    }
}
