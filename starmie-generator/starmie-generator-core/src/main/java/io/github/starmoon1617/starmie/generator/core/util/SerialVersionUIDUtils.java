/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.github.starmoon1617.starmie.generator.core.constant.Constants;

/**
 * Utility Class for SerialVersionUID
 * 
 * @date 2023-10-16
 * @author Nathan Liao
 */
public class SerialVersionUIDUtils {

    private static final DateTimeFormatter SVUID_FORMATTER = DateTimeFormatter.ofPattern(Constants.SVUID_FORMAT);

    /**
     * @return
     */
    public static String getSVUId() {
        long nanoTime = System.nanoTime();
        String svuIdStr = LocalDateTime.now().format(SVUID_FORMATTER);
        long value = (0l ^ (nanoTime & 0x3FF)) << 10;
        value = (value ^ ((nanoTime >> 10) & 0x3FF)) << 7;
        value = (value ^ (Integer.valueOf(svuIdStr.substring(0, 3)).intValue())) << 7;
        value = (value ^ (Integer.valueOf(svuIdStr.substring(3, 5)).intValue())) << 7;
        value = (value ^ (Integer.valueOf(svuIdStr.substring(5, 7)).intValue())) << 7;
        value = (value ^ (Integer.valueOf(svuIdStr.substring(7, 9)).intValue())) << 7;
        value = (value ^ (Integer.valueOf(svuIdStr.substring(9, 11)).intValue())) << 6;
        value = (value ^ (Integer.valueOf(svuIdStr.substring(11, 13)).intValue())) << 5;
        value = (value ^ (Integer.valueOf(svuIdStr.substring(13)).intValue()));
        return Long.toString(value).concat("L");
    }

}
