package com.ambrosia.cluster_controller.util;

import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {
    public static String randName(){
        return "Test"+ThreadLocalRandom.current().nextLong();
    }

    public static Long randLong(){
        return ThreadLocalRandom.current().nextLong();
    }
}
