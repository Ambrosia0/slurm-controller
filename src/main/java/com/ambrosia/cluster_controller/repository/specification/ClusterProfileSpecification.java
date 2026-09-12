package com.ambrosia.cluster_controller.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.ambrosia.cluster_controller.model.entity.ClusterProfile;

public class ClusterProfileSpecification {
    public static Specification<ClusterProfile> usernameContains(String username){
        return (root, query, criteriaBuilder) -> 
            username == null? 
                null:
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("username")), 
                    '%' + username.toLowerCase() + '%'
                );
    }

    public static Specification<ClusterProfile> groupContains(Long groupId){
        return (root, query, criteriaBuilder) -> 
            groupId == null?
                null:
                criteriaBuilder.equal(
                    root.get("id").get("user").get("group").get("id"), 
                    groupId
                );
    }

    public static Specification<ClusterProfile> clusterContains(Long clusterId){
        return (root, query, criteriaBuilder) -> 
            clusterId == null?
                null:
                criteriaBuilder.equal(
                    root.get("id").get("cluster").get("id"), 
                    clusterId
                );
    }

    public static Specification<ClusterProfile> boundContains(String bindedCluster){
        return (root, query, criteriaBuilder) -> 
            bindedCluster == null?
                null:
                criteriaBuilder.equal(
                    root.get("id").get("clusterName"), 
                    bindedCluster
                );
    }

    public static Specification<ClusterProfile> nameContains(String name){
        return (root, query, criteriaBuilder) -> 
            name == null?
                null:
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("id").get("user").get("group").get("name")), 
                    '%' + name.toLowerCase() + '%'
                );
    }
}
