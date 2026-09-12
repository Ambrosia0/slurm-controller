package com.ambrosia.cluster_controller.config.websocket;

import java.net.URI;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component
public class ClusterInterceptor implements HandshakeInterceptor{
    private final ClusterRepository clusterRepository;

    private final SlurmBindedClusterManager bindedClusterManager;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request, 
            ServerHttpResponse response, 
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) throws Exception {
        var id = getClusterId(request.getURI());
        var bindedClusterName = getBindedClusterName(request.getURI());
        if(id == -1L || bindedClusterName == null || bindedClusterManager.getBinded(id, bindedClusterName) == null)
            return false;
        var clusterOpt = clusterRepository.findById(id);
        if(!clusterOpt.isPresent()){
            return false;
        }
        attributes.put("cluster", clusterOpt.get());
        attributes.put("bindedCluster", bindedClusterManager.getBinded(id, bindedClusterName));
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request, 
            ServerHttpResponse response, 
            WebSocketHandler wsHandler,
            Exception exception) {
    }

    private Long getClusterId(URI uri){
        Map<String, String> params = UriComponentsBuilder
                .fromUri(uri)
                .build()
                .getQueryParams()
                .toSingleValueMap();
        return Long.parseLong(params.getOrDefault("clusterId", "-1"));
    }
     
    private String getBindedClusterName(URI uri) {
        Map<String, String> params = UriComponentsBuilder
                .fromUri(uri)
                .build()
                .getQueryParams()
                .toSingleValueMap();
        return params.getOrDefault("clusterName", null);
    }
}
