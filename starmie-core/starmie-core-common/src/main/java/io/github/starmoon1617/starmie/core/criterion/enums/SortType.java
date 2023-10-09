/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.criterion.enums;

/**
 * 
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public enum SortType {
    /**
     * 升序
     */
    ASC("ASC", "A"),
    /**
     * 降序
     */
    DESC("DESC", "D");

    /**
     * value, sql中的排序
     */
    private String value;
    /**
     * type - value缩写
     */
    private String type;

    /**
     * @param value
     * @param type
     */
    private SortType(String value, String type) {
        this.value = value;
        this.type = type;
    }

    /**
     * 获取value
     * 
     * @return
     */
    public String value() {
        return value;
    }

    /**
     * 获取type
     * 
     * @return
     */
    public String type() {
        return type;
    }
}
