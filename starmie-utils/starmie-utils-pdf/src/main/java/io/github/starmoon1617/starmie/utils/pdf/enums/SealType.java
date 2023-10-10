/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.pdf.enums;

/**
 * type of Seal
 * 签章类型
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public enum SealType {
    
    /**
     * 0 - 无签章
     */
    NONE(0, "无签章"),

    /**
     * 1 - 骑缝章
     */
    CROSS_PAGE(1, "骑缝章"),

    /**
     * 2 - 单页签章
     */
    SINGLE_PAGE(2, "单页签章"),

    /**
     * 3 - 每页签章
     */
    ALL_PAGE(3, "每页签章")

    ;

    /**
     * 类型, type
     */
    private Integer type;

    /**
     * 描述, description
     */
    private String desc;

    /**
     * @param type
     * @param desc
     */
    private SealType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }
    
}
