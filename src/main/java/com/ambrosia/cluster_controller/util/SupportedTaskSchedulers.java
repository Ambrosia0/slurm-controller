package com.ambrosia.cluster_controller.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.extern.slf4j.Slf4j;

// перечисление для предоставления классов для взаимодействия с планировщиками задач
@Slf4j
public enum SupportedTaskSchedulers {
    SLURMv0039("v0.0.39"),
    SLURMv0040("v0.0.40"),
    SLURMv0041("v0.0.41"),
    SLURMv0042("v0.0.42");
    
    private String versionName;

    private SupportedTaskSchedulers(String versionName){
        this.versionName = versionName;
    }

    public String getVersion(){
        return versionName;
    }

    @JsonCreator
    public static SupportedTaskSchedulers fromString(String schedulerName){
        for(SupportedTaskSchedulers scheduler: SupportedTaskSchedulers.values()){
            if(scheduler.name().toString().equalsIgnoreCase(schedulerName)){
                return scheduler;
            }
        }
        throw new IllegalArgumentException("Unsupported scheduler: "+ schedulerName);
    }

    public static List<String> getSchedulers(){
        var names = new ArrayList<String>();
        for(SupportedTaskSchedulers scheduler: SupportedTaskSchedulers.values()){
            names.add(scheduler.name());
        }
        return names;
    }
}
