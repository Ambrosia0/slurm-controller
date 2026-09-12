package com.ambrosia.cluster_controller.model.entity;

import com.ambrosia.cluster_controller.util.HttpSchema;
import com.ambrosia.cluster_controller.util.SupportedTaskSchedulers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cluster")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cluster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@SequenceGenerator(name = "cluster_gen", sequenceName = "cluster_id_seq")
    //@GeneratedValue(generator = "cluster_gen", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "hostname")
    private String host;

    @Column(name = "username")
    private String username;

    @Column(name = "password", nullable = true)
    private String password;
    
    @Column(name = "displayed_name")
    private String displayedName;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "http_schema")
    private HttpSchema schema = HttpSchema.HTTP;

    @Builder.Default
    @Column(name = "ssh_port")
    private int sshPort = 22;

    @Builder.Default
    @Column(name = "daemon_port")
    private int daemonPort = 6820;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_scheduler")
    private SupportedTaskSchedulers scheduler;
}
