/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import io.github.starmoon1617.starmie.core.constant.TimeConstants;
import io.github.starmoon1617.starmie.core.exception.DateOperationException;

/**
 * Utility Class for Date
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class DateUtils {

    /**
     * yyyy-MM-dd formatter
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(TimeConstants.DATE_FORMAT);

    /**
     * yyyy-MM-dd HH:mm:ss formatter
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(TimeConstants.DATE_TIME_FORMAT);

    /**
     * yyyy-MM-dd HH:mm:ss.SSS formatter
     */
    public static final DateTimeFormatter DATE_TIME_MS_FORMATTER = DateTimeFormatter.ofPattern(TimeConstants.DATE_TIME_MS_FORMAT);

    private static final ZoneId DEFAULT_ZONEID = ZoneId.systemDefault();

    private DateUtils() {

    }

    public static final Date getCurrentDate() {
        return new Date();
    }

    /**
     * try to using these pattern to parse a Date string <br>
     * (10)"yyyy-MM-dd" <br>
     * (19)"yyyy-MM-dd HH:mm:ss" <br>
     * (23)"yyyy-MM-dd HH:mm:ss.SSS" <br>
     * 
     * @param dateStr
     * @return
     */
    public static final Date parse(String dateStr) {
        if (!CommonUtils.isNotBlank(dateStr)) {
            return null;
        }
        String tmpStr = dateStr.trim();
        try {
            if (tmpStr.trim().length() == 19) {
                return DateUtils.parseDateTime(tmpStr);
            } else if (tmpStr.length() == 10) {
                return DateUtils.parseDate(tmpStr);
            } else {
                return DateUtils.parseDateTimeMs(dateStr);
            }
        } catch (Exception e) {
            throw new DateOperationException("date str is not format of 'yyyy-MM-dd'/'yyyy-MM-dd HH:mm:ss'/'yyyy-MM-dd HH:mm:ss.SSS'!", e);
        }
    }

    /**
     * Parse date str to Date Object with given pattern
     * 
     * @param dateStr
     * @param pattern
     * @return
     */
    public static final Date parse(String dateStr, String pattern) {
        return Date.from(LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern)).atZone(DEFAULT_ZONEID).toInstant());
    }

    /**
     * Parse date str to Date Object with pattern 'yyyy-MM-dd'
     * 
     * @param dateStr
     * @return
     */
    public static final Date parseDate(String dateStr) {
        return Date.from(LocalDateTime.parse(dateStr, DATE_FORMATTER).atZone(DEFAULT_ZONEID).toInstant());
    }

    /**
     * Parse date str to Date Object with pattern 'yyyy-MM-dd HH:mm:ss'
     * 
     * @param dateStr
     * @return
     */
    public static final Date parseDateTime(String dateStr) {
        return Date.from(LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER).atZone(DEFAULT_ZONEID).toInstant());
    }

    /**
     * Parse date str to Date Object with pattern 'yyyy-MM-dd HH:mm:ss.SSS'
     * 
     * @param dateStr
     * @return
     */
    public static Date parseDateTimeMs(String dateStr) {
        return Date.from(LocalDateTime.parse(dateStr, DATE_TIME_MS_FORMATTER).atZone(DEFAULT_ZONEID).toInstant());
    }

    /**
     * format current time to 'yyyy' string
     * 
     * @return
     */
    public static final String formatCurrentYear() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(TimeConstants.YEAR_FORMAT));
    }

    /**
     * format current time to 'yyyy-MM-dd HH:mm:ss' string
     * 
     * @return
     */
    public static final String formatCurrentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    /**
     * format current time to 'yyyy-MM-dd' string
     * 
     * @return
     */
    public static final String formatCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    /**
     * format current time with given pattern
     * 
     * @param pattern
     * @return
     */
    public static final String formatCurrentDate(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * format the given date with given pattern
     * 
     * @param pattern
     * @param date
     * @return
     */
    public static final String format(String pattern, Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONEID).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * format current time to 'MMddHHmmss' long value
     * 
     * @return
     */
    public static final long currentMonthDateTimeStamp() {
        return Long.valueOf(formatCurrentDate(TimeConstants.MONTH_DAY_TIME_STAMP_FORMAT)).longValue();
    }
}
