package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class TaskOwnerNotFoundException extends ApiException{
    public TaskOwnerNotFoundException(){
        super(HttpStatus.BAD_REQUEST, "Task owner not found!");
    }
}
