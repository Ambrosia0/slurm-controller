package com.ambrosia.cluster_controller.exception.api;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.ambrosia.cluster_controller.exception.ApiException;

public class ClusterUnavailableException extends ApiException{
    public ClusterUnavailableException(){
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Cluster unavailable!");
    }
    public ClusterUnavailableException(Map<String, Object> properties){
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Cluster unavailable!", properties);
    }
}
