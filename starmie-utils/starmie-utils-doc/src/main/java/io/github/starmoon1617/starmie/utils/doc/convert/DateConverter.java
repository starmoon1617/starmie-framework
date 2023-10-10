/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.doc.convert;

import java.util.Date;

import io.github.starmoon1617.starmie.core.util.DateUtils;

/**
 * date converter for given pattern
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class DateConverter implements Converter<Date> {
    
    private String pattern;
    
    public DateConverter(String pattern) {
        super();
        this.pattern = pattern;
    }

    @Override
    public String convert(Date t) {
        return DateUtils.format(pattern, t);
    }

}
