package com.ambrosia.cluster_controller.repository.custom.impl;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.ambrosia.cluster_controller.model.DTO.filters.GroupFilter;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.model.entity.Group;
import com.ambrosia.cluster_controller.repository.custom.CustomGroupRepository;
import com.ambrosia.cluster_controller.repository.specification.ClusterProfileSpecification;
import com.ambrosia.cluster_controller.repository.specification.GroupSpecification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Repository 
public class CustomGroupRepositoryImpl implements CustomGroupRepository{
    private final EntityManager entityManager;

    @Override
    public List<Group> search(GroupFilter groupFilter) {
        var criteriaBuider = entityManager.getCriteriaBuilder();
        var query = criteriaBuider.createQuery(Group.class);

        Root<ClusterProfile> profileRoot;
        Specification<ClusterProfile> profileSpec;
        
        Root<Group> groupRoot;
        Specification<Group> groupSpec;

        Predicate pred;
        if(groupFilter.clusterId() != null || groupFilter.bindedCluster() != null){
            profileRoot = query.from(ClusterProfile.class);
            query.select(
                profileRoot.get("id").get("user").get("group")
            ).distinct(true);
            profileSpec = Specification.<ClusterProfile>unrestricted()
                .and(ClusterProfileSpecification.clusterContains(groupFilter.clusterId()))
                .and(ClusterProfileSpecification.boundContains(groupFilter.bindedCluster()))
                .and(ClusterProfileSpecification.nameContains(groupFilter.name()));
            pred = profileSpec.toPredicate(profileRoot, query, criteriaBuider);
        }else{
            groupRoot = query.from(Group.class);
            groupSpec = Specification.<Group>unrestricted()
                .and(GroupSpecification.nameContains(groupFilter.name()));
            pred = groupSpec.toPredicate(groupRoot, query, criteriaBuider);
        }
        if(pred != null){
            query.where(pred);
        }   
        return entityManager
            .createQuery(query)
            .getResultList();
    }
}
