package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class ProfileDoesntExistException extends ApiException{
    public ProfileDoesntExistException(Long userId, Long clusterId){
        super(HttpStatus.BAD_REQUEST, "User profile with userId "+userId+" and clusterId "+clusterId+" doesnt exist!");
    }
    public ProfileDoesntExistException(){
        super(HttpStatus.BAD_REQUEST, "User profile with doesnt exist!");
    }
}
