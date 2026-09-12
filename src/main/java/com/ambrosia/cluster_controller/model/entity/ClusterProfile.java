package com.ambrosia.cluster_controller.model.entity;

import java.time.Instant;
import java.util.List;

import com.ambrosia.cluster_controller.model.entity.compositeKeys.ClusterProfileKey;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cluster_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClusterProfile {
    @EmbeddedId
    private ClusterProfileKey id;

    @Column(name = "password")
    private String password;

    @Column(name = "profile_id")
    private Integer profileId;

    @Builder.Default
    @Column(name = "max_submit")
    private Integer maxSubmit = 2;

    @Builder.Default
    @Column(name = "max_tasks")
    private Integer maxTasks = 1;

    @Builder.Default
    @Column(name = "max_tres")
    private List<String> maxTres = List.of("cpu=1","mem=128","node=1");

    @Builder.Default
    @Column(name = "max_task_ttl")
    private Integer maxTaskTtl = 1024;

    @Builder.Default
    @Column(name = "soft_limit")
    private Long softLimit = 1024L;

    @Builder.Default
    @Column(name = "hard_limit")
    private Long hardLimit = 2048L;

    @Column(name = "created_at", insertable = false)
    private Instant createdAt;
}
