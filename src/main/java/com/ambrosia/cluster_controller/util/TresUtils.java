package com.ambrosia.cluster_controller.util;

public class TresUtils {
    public final static String TRES_CSV_PATTERN = "^[^,=]+=[^,]+(?:,[^,=]+=[^,]+)*$";
    public final static String TRES_PATTERN = "^[^,=]+=[0-9]+$";
    public final static String GRES_CSV_PATTERN = "^gres/[^=;]+=[0-9]+(?:;gres/[^=;]+=[0-9]+)*$";
}
