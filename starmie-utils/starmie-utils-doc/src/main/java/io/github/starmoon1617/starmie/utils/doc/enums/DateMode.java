/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.doc.enums;

/**
 * Default mode to write date
 * 
 * @date 2023-11-17
 * @author Nathan Liao
 */
public enum DateMode {

    /**
     * NONE : 0 - no pattern
     */
    NONE(0 , ""),
    
    /**
     * Date : 1 - yyyy-MM-dd
     */
    DATE(1, "yyyy-MM-dd"),
    
    /**
     * Date time : 2 - yyyy-MM-dd HH:mm:ss
     */
    DATETIME(2, "yyyy-MM-dd HH:mm:ss");
    
    /**
     * mode name
     */
    private Integer mode;

    /**
     * date pattern
     */
    private String pattern;

    /**
     * @param mode
     * @param pattern
     */
    private DateMode(Integer mode, String pattern) {
        this.mode = mode;
        this.pattern = pattern;
    }

    /**
     * @return the mode
     */
    public Integer getMode() {
        return mode;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

}
