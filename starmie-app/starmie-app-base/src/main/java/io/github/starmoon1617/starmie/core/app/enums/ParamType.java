/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.app.enums;

/**
 * type for parameters
 * 
 * @date 2023-10-23
 * @author Nathan Liao
 */
public enum ParamType {

    /**
     * Integer
     */
    INT("I", "Integer"),
    /**
     * Long
     */
    LONG("L", "Long"),
    /**
     * String
     */
    STRING("S", "String"),
    /**
     * Short
     */
    SHORT("H", "short"),
    /**
     * Date
     */
    DATE("D", "date"),
    /**
     * Datetime
     */
    DATETIME("T", "DateTime"),
    /**
     * Bigdecimal
     */
    BIGDECIMAL("B", "BigDecimal");

    /**
     * type
     */
    private String type;
    /**
     * name
     */
    private String value;

    private ParamType(String type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * @return the type
     */
    public String type() {
        return type;
    }

    /**
     * @return the name
     */
    public String value() {
        return value;
    }

}
