package com.ambrosia.cluster_controller.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{
    private final HttpStatus httpStatus;

    private Map<String, Object> properties;

    public ApiException(HttpStatus httpStatus, String message){
        super(message);
        this.httpStatus = httpStatus;
    }

    public ApiException(HttpStatus httpStatus, String message, Map<String, Object> properties){
        super(message);
        this.httpStatus = httpStatus;
        this.properties = properties;
    }
}
