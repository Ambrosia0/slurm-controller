package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class AccessDoesntProvidedException extends ApiException{
    public AccessDoesntProvidedException(){
        super(HttpStatus.FORBIDDEN, "Access doesn't provided");
    }
}
