package com.ambrosia.cluster_controller.exception.api;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class CantCreateProfileException extends ApiException{
    public CantCreateProfileException(Map<String, Object> properties){
        super(HttpStatus.BAD_REQUEST, "Can't create profiles!", properties);
    }
}
