package com.ambrosia.cluster_controller.exception.api;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class CantPollTaskException extends ApiException{
    public CantPollTaskException(Map<String, Object> properties){
        super(HttpStatus.BAD_REQUEST, "Can't poll tasks!", properties);
    }
}
