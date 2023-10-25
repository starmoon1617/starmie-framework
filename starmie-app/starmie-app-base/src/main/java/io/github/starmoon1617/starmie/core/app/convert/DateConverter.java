/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.app.convert;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

import io.github.starmoon1617.starmie.core.util.DateUtils;

/**
 * Convert date for 'yyyy-MM-dd'/'yyyy-MM-dd HH:mm:ss'/'yyyy-MM-dd HH:mm:ss.SSS'
 * 
 * @date 2023-10-24
 * @author Nathan Liao
 */
public class DateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        return DateUtils.parse(source);
    }

}
