package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class BindedClusterDoesntExistException extends ApiException{
    public BindedClusterDoesntExistException(){
        super(HttpStatus.BAD_REQUEST, "Binded cluster doesn't exist!");
    }
}
