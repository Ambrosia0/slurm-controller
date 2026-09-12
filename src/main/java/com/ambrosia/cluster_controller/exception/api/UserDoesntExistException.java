package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class UserDoesntExistException extends ApiException{
    public UserDoesntExistException(Long id){
        super(HttpStatus.BAD_REQUEST, "User "+ id +" doesnt exist!");
    }
    public UserDoesntExistException(){
        super(HttpStatus.BAD_REQUEST, "User doesnt exist!");
    }
}
