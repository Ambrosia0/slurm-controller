package com.ambrosia.cluster_controller.repository.specification;

import java.util.ArrayList;

import org.springframework.data.jpa.domain.Specification;

import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.model.entity.Group;

import jakarta.persistence.criteria.Predicate;

public class GroupSpecification {
    public static Specification<Group> nameContains(String name){
        return name == null?
            null:
            (root, query, criteriaBuilder) -> 
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), 
                    '%' + name.toLowerCase() + '%'
                );
    }

    public static Specification<Group> clusterContains(Long clusterId, String bindedCluster, boolean notInCluster){
        return clusterId == null?
            null:
            (root, query, criteriaBuilder) -> {
                var predicates = new ArrayList<Predicate>();
                var subquery = query.subquery(Long.class);
                var profile = subquery.from(ClusterProfile.class);
                
                predicates.add(
                    criteriaBuilder.equal(
                        profile.get("id").get("cluster").get("id"),
                        clusterId
                    )
                );

                predicates.add(
                    criteriaBuilder.equal(
                        profile.get("id").get("user").get("group").get("id"), 
                        root.get("id")
                    )
                );

                if(bindedCluster != null)
                    predicates.add(
                        criteriaBuilder.equal(
                            profile.get("id").get("clusterName"), 
                            bindedCluster
                        )
                    );
                subquery.select(criteriaBuilder.literal(1L))
                    .where(predicates.toArray(Predicate[]::new));
                return notInCluster?
                    criteriaBuilder.not(criteriaBuilder.exists(subquery)):
                    criteriaBuilder.exists(subquery);
            };
    }
}
