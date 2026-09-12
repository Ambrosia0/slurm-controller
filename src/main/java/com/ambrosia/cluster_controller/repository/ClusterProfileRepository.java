package com.ambrosia.cluster_controller.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ambrosia.cluster_controller.model.DTO.ProfileCount;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.model.entity.compositeKeys.ClusterProfileKey;
import com.ambrosia.cluster_controller.repository.custom.CustomClusterProfileRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface ClusterProfileRepository extends 
    JpaRepository<ClusterProfile, ClusterProfileKey>,
    JpaSpecificationExecutor<ClusterProfile>,
    CustomClusterProfileRepository {
    @Query("""
        SELECT pf FROM ClusterProfile pf
        JOIN FETCH pf.id.cluster cl
        JOIN FETCH pf.id.user us
        where pf.id.cluster.id = :clusterId 
    """)
    Page<ClusterProfile> findByClusterId(@Param("clusterId") long clusterId, Pageable pageable);

    @Query("""
        SELECT pf FROM ClusterProfile pf
        JOIN FETCH pf.id.cluster cl
        JOIN FETCH pf.id.user us
        where pf.id.user.id = :userId 
    """)
    List<ClusterProfile> findByUserId(@Param("userId") long userId);
    
    @Query("""
        SELECT COUNT(pf)>0 from ClusterProfile pf 
        where pf.id.cluster.id = :clusterId
        and pf.id.user.id = :userId
        and pf.id.clusterName = :clusterName
    """)
    boolean existsByIds(
        @Param("clusterId") long clusterId, 
        @Param("userId") long userId,
        @Param("clusterName") String clusterName
    );

    @Query("""
        SELECT pf FROM ClusterProfile pf
        JOIN FETCH pf.id.cluster cl
        JOIN FETCH pf.id.user us
        where pf.id.cluster.id = :clusterId
        and pf.id.user.id IN :userIds
        and pf.id.clusterName = :clusterName
    """)
    List<ClusterProfile> findByIds(
        @Param("clusterId") long clusterId, 
        @Param("clusterName") String clusterName,
        @Param("userIds") Set<Long> userIds
    );

    @Query("""
        SELECT pf.id.user.id, COUNT(pf) FROM ClusterProfile pf
        WHERE pf.id.cluster.id = :clusterId
        AND pf.id.user.id IN :userIds
        GROUP BY pf.id.user.id
    """)
    List<ProfileCount> countByClusterId(
        @Param("clusterId") Long clusterId,
        @Param("userIds") Set<Long> userIds
    );

    @Query("""
        SELECT pf FROM ClusterProfile pf
        JOIN FETCH pf.id.cluster cl
        JOIN FETCH pf.id.user us
        where pf.id.cluster.id = :clusterId
        and pf.id.user.id = :userId
        and pf.id.clusterName = :clusterName
    """)
    Optional<ClusterProfile> findByIdsWS(
        @Param("clusterId") long clusterId, 
        @Param("userId") long userId,
        @Param("clusterName") String name);

    @Query("""
        SELECT pf FROM ClusterProfile pf
        JOIN FETCH pf.id.cluster cl
        JOIN FETCH pf.id.user us
        where pf.id.cluster.id = :clusterId
        and pf.id.clusterName = :bindedCluster
        and pf.id.user.id in :userIds
    """)
    List<ClusterProfile> findByClusterIdAndBindedClusterAndProfileIds(
        @Param("clusterId") long clusterId,
        @Param("bindedCluster") String bindedCluster,
        @Param("userIds") Set<Long> userIds);

    @Query("""
        SELECT DISTINCT(pf.id.user.username) FROM ClusterProfile pf
        where pf.id.cluster.id = :clusterId
        and pf.id.user.group.id = :groupId
    """)
    List<String> findUsernamesByClusterIdAndGroupId(long clusterId, Long groupId);

    @Query("""
        SELECT pf FROM ClusterProfile pf
        JOIN FETCH pf.id.cluster cl
        JOIN FETCH pf.id.user us
        where pf.id.cluster.id = :clusterId
        and pf.id.user.group.id = :groupId
        and pf.id.clusterName = :clusterName
    """)
    List<ClusterProfile> findByClusterIdAndGroupIdAndClusterName(
        @Param("clusterId") long clusterId,
        @Param("groupId") long groupId,
        @Param("clusterName") String clusterName);


    @Query("""
        SELECT pf FROM ClusterProfile pf
        JOIN FETCH pf.id.cluster cl
        JOIN FETCH pf.id.user us
        WHERE pf.id.cluster.id = :clusterId
    """)
    List<ClusterProfile> findByClusterId(@Param("clusterId") long clusterId);

    @Query("""
        SELECT pf.id.clusterName FROM ClusterProfile pf
        where pf.id.cluster.id = :clusterId
        and pf.id.user.id = :userId
    """)
    Set<String> findBindedClustersForUser(
        @Param("clusterId") long clusterId,
        @Param("userId") long userId);

    @Query("""
        SELECT pf FROM ClusterProfile pf
        JOIN FETCH pf.id.cluster cl
        JOIN FETCH pf.id.user us
        WHERE us.group.id = :groupId
    """)
    List<ClusterProfile> findByGroupId(long groupId);

    @Query("""
       SELECT COUNT(pf) > 0 FROM ClusterProfile pf
       WHERE pf.id.cluster.id = :clusterId
       AND pf.id.user.id IN :userIds
       AND pf.id.clusterName = :clusterName
    """)
    boolean existsByIds(Long clusterId, String clusterName, Set<Long> userIds);
    
    @Query("""
        SELECT pf FROM ClusterProfile pf
        JOIN FETCH pf.id.cluster cl
        JOIN FETCH pf.id.user us
        WHERE pf.id.cluster.id = :clusterId
        AND pf.id.user.id = :userId
        AND pf.id.clusterName = :clusterName
    """)
    Optional<ClusterProfile> findById(Long clusterId, Long userId, String clusterName);

    @Query("""
        SELECT pf FROM ClusterProfile pf
        JOIN FETCH pf.id.cluster cl
        JOIN FETCH pf.id.user us
        WHERE pf.id.cluster.id = :clusterId
        AND pf.id.user.id = :userId
    """)
    Optional<ClusterProfile> findFirstByClusterIdAndUserId(Long clusterId, Long userId);
}
