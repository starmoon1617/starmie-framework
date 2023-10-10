/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.json;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.github.starmoon1617.starmie.core.util.DateUtils;

/**
 * Common JsonDeserializer for Date
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class DateJsonDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return DateUtils.parse(p.getValueAsString());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

}
