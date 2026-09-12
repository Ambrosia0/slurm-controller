package com.ambrosia.cluster_controller.repository.custom.impl;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.repository.custom.CustomClusterProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor 
@Repository 
public class CustomClusterProfileRepositoryImpl implements  CustomClusterProfileRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<ClusterProfile> clusterProfiles) {
        var sql = """
            INSERT INTO cluster_profile(
                cluster_id, 
                user_id, 
                profile_id, 
                cluster_name, 
                password, 
                max_submit, 
                max_tasks, 
                max_tres,
                max_task_ttl, 
                soft_limit, 
                hard_limit
            ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(ClusterProfile clusterProfile: clusterProfiles){
                stmt.setLong(1, clusterProfile.getId().getCluster().getId());
                stmt.setLong(2, clusterProfile.getId().getUser().getId());
                stmt.setLong(3, clusterProfile.getProfileId());
                stmt.setString(4, clusterProfile.getId().getClusterName());
                stmt.setString(5, clusterProfile.getId().getUser().getPassword());
                stmt.setInt(6, clusterProfile.getMaxSubmit());
                stmt.setInt(7, clusterProfile.getMaxTasks());
                stmt.setArray(
                    8, 
                    conn.createArrayOf("text", clusterProfile.getMaxTres().toArray())
                );
                stmt.setInt(9, clusterProfile.getMaxTaskTtl());
                stmt.setLong(10, clusterProfile.getSoftLimit());
                stmt.setLong(11, clusterProfile.getHardLimit());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't execute batched insert on cluster profiles! {}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void batchUpdate(List<ClusterProfile> clusterProfiles) {
        var sql = """
            UPDATE cluster_profile
            SET
                max_submit = ?,
                max_tasks = ?,
                max_tres = ?,
                max_task_ttl = ?,
                soft_limit = ?,
                hard_limit = ?
            WHERE cluster_id = ?
            AND user_id = ?
            AND cluster_name = ?
        """;
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(ClusterProfile clusterProfile: clusterProfiles){
                stmt.setInt(1, clusterProfile.getMaxSubmit());
                stmt.setInt(2, clusterProfile.getMaxTasks());
                stmt.setArray(
                    3, 
                    conn.createArrayOf("text", clusterProfile.getMaxTres().toArray())
                );
                stmt.setInt(4, clusterProfile.getMaxTaskTtl());
                stmt.setLong(5, clusterProfile.getSoftLimit());
                stmt.setLong(6, clusterProfile.getHardLimit());
                stmt.setLong(7, clusterProfile.getId().getCluster().getId());
                stmt.setLong(8, clusterProfile.getId().getUser().getId());
                stmt.setString(9, clusterProfile.getId().getClusterName());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't execute batched update on cluster profiles! {}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void batchDelete(List<ClusterProfile> clusterProfiles) {
        var sql = """
        DELETE FROM cluster_profile
        WHERE cluster_id = ?
        AND user_id = ?
        AND cluster_name = ?       
        """;
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(ClusterProfile clusterProfile: clusterProfiles){
                stmt.setLong(1, clusterProfile.getId().getCluster().getId());
                stmt.setLong(2, clusterProfile.getId().getUser().getId());
                stmt.setString(3, clusterProfile.getId().getClusterName());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't execute batched delete on cluster profiles! {}", e);
            throw new RuntimeException(e);
        }
    }
}
