package com.ambrosia.cluster_controller.util.factory;

import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.cluster_controller.model.entity.Group;

public class GroupFactory {
    public static Group create(){
        return Group.builder()
            .name("TestGroup"+ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
            .build();
    }
}
