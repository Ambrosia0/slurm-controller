package com.ambrosia.cluster_controller.exception.api;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class CantDeleteProfileException extends ApiException{
    public CantDeleteProfileException(){
        super(HttpStatus.BAD_REQUEST, "Can't delete profile!");
    }

    public CantDeleteProfileException(Map<String, Object> properties){
        super(HttpStatus.BAD_REQUEST, "Can't delete profile!", properties);
    }
}
