/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.pdf.enums;

/**
 * Page Orientation
 * 纸张方向
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public enum PageOrientation {
    
    /**
     * 1 - 纵向
     */
    PORTRAIT(1, "Portrait", "纵向"),

    /**
     * 2 - 横向
     */
    LANDSCAPE(2, "Landscape", "横向");
    
    /**
     * 类型
     */
    private int type;

    /**
     * 类型, 英文
     */
    private String orientation;

    /**
     * 描述
     */
    private String desc;

    private PageOrientation(int type, String orientation, String desc) {
        this.type = type;
        this.orientation = orientation;
        this.desc = desc;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @return the orientation
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

}
