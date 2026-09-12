package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class ExceededAvailableResourcesException extends ApiException{
    public ExceededAvailableResourcesException(){
        super(HttpStatus.CONFLICT, "Exceeded available resources!");
    }
}
