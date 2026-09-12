package com.ambrosia.cluster_controller.model.entity.compositeKeys;

import java.io.Serializable;

import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder 
@Embeddable
public class ClusterProfileKey implements Serializable{
    @ManyToOne
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "cluster_name")
    private String clusterName;
}
