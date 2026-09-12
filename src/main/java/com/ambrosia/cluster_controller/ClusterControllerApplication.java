package com.ambrosia.cluster_controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClusterControllerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClusterControllerApplication.class, args);
	}
}
