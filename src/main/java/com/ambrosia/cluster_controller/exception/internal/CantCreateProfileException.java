package com.ambrosia.cluster_controller.exception.internal;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

import lombok.Getter;

@Getter
public class CantCreateProfileException extends ApiException {
    private String sshMessage;
    public CantCreateProfileException(){
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Can't create profile!");
    }
}
