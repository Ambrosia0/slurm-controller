package com.ambrosia.cluster_controller.exception.api;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class CantCancelTaskException extends ApiException {
    
    public CantCancelTaskException(Map<String, Object> properties){
        super(HttpStatus.BAD_REQUEST, "Can't cancel task!", properties);
    }

    public CantCancelTaskException(){
        super(HttpStatus.BAD_REQUEST, "Can't cancel task!");
    }
}
