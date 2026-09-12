package com.ambrosia.cluster_controller.exception.api;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class CantCreateTaskException extends ApiException{
    public CantCreateTaskException(String message, Map<String, Object> properties){
        super(HttpStatus.BAD_REQUEST, message, properties);
    }
}
