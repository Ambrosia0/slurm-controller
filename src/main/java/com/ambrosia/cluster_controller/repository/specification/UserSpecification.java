package com.ambrosia.cluster_controller.repository.specification;

import java.util.ArrayList;

import org.springframework.data.jpa.domain.Specification;

import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.model.entity.User;
import com.ambrosia.cluster_controller.util.Role;

import jakarta.persistence.criteria.Predicate;

public class UserSpecification {
    public static Specification<User> user(){
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(
                root.get("role"), 
                Role.ROLE_USER    
            );
    }

    public static Specification<User> usernameContains(String username){
        return username == null?
            null:
            (root, query, criteriaBuilder) -> 
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("username")),
                    '%' + username.toLowerCase() + '%'
                );
    }

    public static Specification<User> containsProfileInCluster(Long clusterId, String bindedCluster, boolean notInCluster){
        return clusterId == null?
            null:
            (root, query, criteriaBuilder) -> {
                var subquery = query.subquery(Long.class);
                var profile = subquery.from(ClusterProfile.class);
                var predicates = new ArrayList<Predicate>();
                
                predicates.add(
                    criteriaBuilder.equal(
                        profile.get("id").get("cluster").get("id"), 
                        clusterId
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

    public static Specification<User> groupContains(Long groupId){
        return groupId == null?
            null:
            (root, query, criteriaBuilder) -> 
                criteriaBuilder.equal(
                    root.get("group").get("id"), 
                    groupId  
                );
    }

    public static Specification<User> doesntContainGroup(){
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.isNull(root.get("group"));
    }
}
