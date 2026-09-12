package com.ambrosia.cluster_controller.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.ambrosia.cluster_controller.model.entity.Cluster;

public class ClusterSpecification {
    public static Specification<Cluster> displayedNameContains(String displayedName){
        return displayedName == null?
                null:
                (root, query, criteriaBuilder) -> 
                    criteriaBuilder.like(root.get("displayedName"), '%' + displayedName + '%');

    }
}
