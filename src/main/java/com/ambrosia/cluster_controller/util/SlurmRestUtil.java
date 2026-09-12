package com.ambrosia.cluster_controller.util;

import org.springframework.web.util.UriComponentsBuilder;

public class SlurmRestUtil {
    public static String CLUSTER_URL = "{schema}://{hostname}:{daemonPort}/slurm/{slurmVersion}";
    public static String DATABASE_URL = "{schema}://{hostname}:{daemonPort}/slurmdb/{slurmVersion}";
    
    public static UriComponentsBuilder buildSlurmDaemonRequest(){
        return UriComponentsBuilder.fromUriString(
           CLUSTER_URL+"/{rest}"
        );
    }

    public static UriComponentsBuilder buildDbDaemonRequest(){
        return UriComponentsBuilder.fromUriString(
            DATABASE_URL+"/{rest}"
        );
    }

}
