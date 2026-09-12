package com.ambrosia.cluster_controller.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ambrosia.cluster_controller.model.entity.Group;
import com.ambrosia.cluster_controller.model.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;


public interface UserRepository extends 
        JpaRepository<User, Long>,
        JpaSpecificationExecutor<User>{
    
    @Query("""
        SELECT us.id FROM User us where us.role = "ROLE_ADMIN"    
    """)
    Set<Long> findAdminIds();

    Optional<User> findByUsername(String username);

    @Modifying
    @Query("""
        DELETE FROM User us where us.group = :group
    """)
    void deleteByGroup(@Param("group") Group group);

    @Query("""
        SELECT COUNT(us)>0 FROM User us where us.username ILIKE :username
    """)
    boolean existsByUsername(@Param("username") String username);

    @Query("""
        SELECT us FROM User us where us.group.id = :groupId    
    """)
    List<User> findByGroupId(@Param("groupId") long groupId);

    @Modifying
    @Query("""
        UPDATE User us SET us.group = :group where us.id IN :userIds
    """)
    void groupUsers(@Param("group") Group group, @Param("userIds") Set<Long> userIds);

    @Modifying
    @Query("""
        UPDATE User us SET us.group = null where us.id IN :userIds
    """)
    void ungroupUsers(@Param("userIds") Set<Long> userIds);

    @Query("""
        SELECT COUNT(us) = :size FROM User us where us.id IN :userIds 
    """)
    boolean compareSize(@Param("userIds") Set<Long> userIds, int size);

    @Query("""
        SELECT us FROM User us WHERE us.id IN :userIds
    """)
    List<User> findByIds(Set<Long> userIds);

    @Query("""
        SELECT us FROM User us 
        where us.username ILIKE CONCAT('%', :username, '%')
        and us.role = 'ROLE_USER'
    """)
    Page<User> findByUsernamePartially(@Param("username") String username, Pageable pageable);

    @Query("""
        SELECT us FROM User us where us.role != "ROLE_ADMIN" 
        """)
    Stream<User> streamAll();

    @Query("""
        SELECT us FROM User us where us.group = :group
        """)
    Stream<User> streamGroup(@Param("group") Group group);

    @Query("""
            SELECT us FROM User us where us.id 
                not in (select pf.id.user.id FROM ClusterProfile pf where pf.id.cluster.id = :clusterId) 
            and us.username ILIKE CONCAT('%', :username, '%')
        """)
    Page<User> searchUnbinded(
        @Param("clusterId") long clusterId, 
        @Param("username") String username, 
        Pageable pageable);

    @Query("""
            SELECT us FROM User us where us.group.id = :groupId 
            and us.id not in (select pf.id.user.id FROM ClusterProfile pf where pf.id.cluster.id = :clusterId and pf.id.user.group.id = :groupId)     
        """)
    List<User> findUnbindedInGroup(@Param("clusterId") long clusterId, @Param("groupId") long groupId);

    @Query("""
            SELECT us FROM User us
            where us.role = 'ROLE_USER'  
        """)
    Page<User> findUsers(Pageable pageable);
}
