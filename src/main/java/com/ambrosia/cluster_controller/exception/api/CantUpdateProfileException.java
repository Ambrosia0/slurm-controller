package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class CantUpdateProfileException extends ApiException{
    public CantUpdateProfileException(){
        super(HttpStatus.BAD_REQUEST, "Can't update profile!");
    }
}
