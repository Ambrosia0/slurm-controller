package com.ambrosia.cluster_controller.util;

public enum HttpSchema {
    HTTP("http"),
    HTTPS("https");

    private String name;

    private HttpSchema(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
