package com.ambrosia.cluster_controller.repository;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ambrosia.cluster_controller.model.entity.Cluster;

public interface ClusterRepository extends 
        JpaRepository<Cluster, Long>,
        JpaSpecificationExecutor<Cluster> {
    boolean existsByHost(String host);
    
    @Query("SELECT cl from Cluster cl where cl.scheduler ILIKE CONCAT('%', :scheduler, '%')")
    List<Cluster> findClustersByTaskSchedulerString(@Param("scheduler") String taskScheduler);

    @Query("""
        SELECT pf.id.cluster FROM ClusterProfile pf where pf.id.user.id = :userId
     """)
    List<Cluster> findByUserId(@Param("userId") long userId);

    @Query("""
        SELECT cl FROM Cluster cl where cl.displayedName ILIKE CONCAT('%', :name, '%')
    """)
    List<Cluster> searchByDisplayedName(@Param("name") String name);

    @Query("""
        SELECT cl.id FROM Cluster cl
     """)
    Stream<Long> streamAllIds();

    @Query("""
        SELECT cl FROM Cluster cl    
    """)
    Stream<Cluster> streamAll();
}
