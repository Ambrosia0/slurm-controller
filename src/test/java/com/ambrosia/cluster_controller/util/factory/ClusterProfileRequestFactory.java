package com.ambrosia.cluster_controller.util.factory;

import java.util.List;

import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterProfileAdminRequest;

public class ClusterProfileRequestFactory {
    public static ClusterProfileAdminRequest create(Long userId){
        return new ClusterProfileAdminRequest(
            userId,
            1,
            1,
            List.of("cpu=1", "mem=128","node=1"),
            1024L, 
            2048L,
            1024
        );
    }
}
