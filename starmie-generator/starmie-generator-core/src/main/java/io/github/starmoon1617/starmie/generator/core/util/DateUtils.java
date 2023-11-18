/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 * 
 */
package io.github.starmoon1617.starmie.generator.core.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import io.github.starmoon1617.starmie.generator.core.constant.Constants;

/**
 * Utility Class for Date Object
 * 
 * @date 2023-11-17
 * @author Nathan Liao
 */
public class DateUtils {

    /**
     * yyyy-MM-dd formatter
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    /**
     * yyyy-MM-dd HH:mm:ss formatter
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

    /**
     * format current time to 'yyyy-MM-dd' string
     * 
     * @return
     */
    public static final String getDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    /**
     * format current time to 'yyyy-MM-dd HH:mm:ss' string
     * 
     * @return
     */
    public static final String getDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    /**
     * get current Date object
     * 
     * @return
     */
    public static final Date getCurrentDate() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

}
