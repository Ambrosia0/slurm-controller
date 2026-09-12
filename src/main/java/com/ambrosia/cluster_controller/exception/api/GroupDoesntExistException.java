package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class GroupDoesntExistException extends ApiException {
    public GroupDoesntExistException(){
        super(HttpStatus.BAD_REQUEST, "Group doesnt exist!");
    }
}
