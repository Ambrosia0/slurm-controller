package com.ambrosia.cluster_controller.exception.api;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class ClusterDoesntExistException extends ApiException{
    public ClusterDoesntExistException(Long id){
        super(HttpStatus.BAD_REQUEST, id == null? "Cluster doesn't exist!": "Cluster with id "+id+" doesn't exist!");
    }
        public ClusterDoesntExistException(){
        super(HttpStatus.BAD_REQUEST, "Cluster doesn't exist!");
    }
}
