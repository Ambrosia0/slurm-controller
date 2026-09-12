package com.ambrosia.cluster_controller.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ambrosia.cluster_controller.model.DTO.admin.response.GroupAdminResponse;
import com.ambrosia.cluster_controller.model.entity.Group;
import com.ambrosia.cluster_controller.repository.custom.CustomGroupRepository;

public interface GroupRepository extends 
        JpaRepository<Group, Long>, 
        CustomGroupRepository,
        JpaSpecificationExecutor<Group>{
    Optional<Group> findByName(String name);
    boolean existsByName(String name);

    @Query("""
            SELECT distinct pf.id.user.group FROM ClusterProfile pf
            where pf.id.cluster.id = :clusterId
            and pf.id.clusterName = :clusterName
        """)
    Page<GroupAdminResponse> findByExistingProfilesOnCluster(
        @Param("clusterId") long clusterId, 
        @Param("clusterName") String clusterName,
        Pageable pageable
    );

    @Query("""
        SELECT distinct pf.id.user.group FROM ClusterProfile pf
        where pf.id.cluster.id = :clusterId
        and pf.id.clusterName = :clusterName
        and pf.id.user.group.name ILIKE CONCAT('%', :name, '%')
    """)
    Page<GroupAdminResponse> searchGroupsOnClusterByName(
        @Param("clusterId") Long clusterId,
        @Param("clusterName") String clusterName,
        @Param("name") String name,
        Pageable pageable
    );

    @Query("""
        SELECT g FROM Group g
        WHERE g.name ILIKE CONCAT('%', :name, '$')
    """)
    Page<GroupAdminResponse> searchByName(
        @Param("name") String name,
        Pageable pageable
    );
}
