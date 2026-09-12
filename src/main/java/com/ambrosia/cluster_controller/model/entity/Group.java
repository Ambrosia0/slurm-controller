package com.ambrosia.cluster_controller.model.entity;

import java.time.Instant;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_group")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "group_name")
    private String name;

    @Column(name = "created_at", insertable = false)
    private Instant createdAt;

}
