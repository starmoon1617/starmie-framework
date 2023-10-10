/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.pdf.enums;

/**
 * PDF page type
 * 纸张类型, 定义纸张大小, 单位(毫米,mm)
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public enum PageType {
    /**
     * A0 : 841 x 1189 (mm)
     */
    A0("A0", 841, 1189),
    /**
     * A1 : 594 x 841 (mm)
     */
    A1("A1", 594, 841),
    /**
     * A2 : 420 x 594 (mm)
     */
    A2("A2", 420, 594),
    /**
     * A3 : 297 x 420 (mm)
     */
    A3("A3", 297, 420),
    /**
     * A4 : 210 x 297 (mm)
     */
    A4("A4", 210, 297),
    /**
     * A5 : 148 x 210 (mm)
     */
    A5("A5", 148, 210),
    /**
     * A6 : 105 x 148 (mm)
     */
    A6("A6", 105, 148),
    /**
     * A7 : 74 x 105 (mm)
     */
    A7("A7", 74, 105),
    /**
     * A8 : 52 x 74 (mm)
     */
    A8("A8", 52, 74),
    /**
     * A9 : 37 x 52 (mm)
     */
    A9("A9", 37, 52),
    /**
     * A10 : 26 x 37 (mm)
     */
    A10("A10", 26, 37),
    /**
     * B0 : 1000 x 1414 (mm)
     */
    B0("B0", 1000, 1414),
    /**
     * B1 : 707 x 1000 (mm)
     */
    B1("B1", 707, 1000),
    /**
     * B2 : 500 x 707 (mm)
     */
    B2("B2", 500, 707),
    /**
     * B3 : 353 x 500 (mm)
     */
    B3("B3", 353, 500),
    /**
     * B4 : 250 x 353 (mm)
     */
    B4("B4", 250, 353),
    /**
     * B5 : 176 x 250 (mm)
     */
    B5("B5", 176, 250),
    /**
     * B6 : 125 x 176 (mm)
     */
    B6("B6", 125, 176),
    /**
     * B7 : 88 x 125 (mm)
     */
    B7("B7", 88, 125),
    /**
     * B8 : 62 x 88 (mm)
     */
    B8("B8", 62, 88),
    /**
     * B9 : 44 x 62 (mm)
     */
    B9("B9", 44, 62),
    /**
     * B10 : 31 x 44 (mm)
     */
    B10("B10", 31, 44),
    /**
     * C0 : 917 x 1297 (mm)
     */
    C0("C0", 917, 1297),
    /**
     * C1 : 648 x 917 (mm)
     */
    C1("C1", 648, 917),
    /**
     * C2 : 458 x 648 (mm)
     */
    C2("C2", 458, 648),
    /**
     * C3 : 324 x 458 (mm)
     */
    C3("C3", 324, 458),
    /**
     * C4 : 229 x 324 (mm)
     */
    C4("C4", 229, 324),
    /**
     * C5 : 162 x 229 (mm)
     */
    C5("C5", 162, 229),
    /**
     * C6 : 114 x 162 (mm)
     */
    C6("C6", 114, 162),
    /**
     * C7 : 81 x 114 (mm)
     */
    C7("C7", 81, 114),
    /**
     * C8 : 57 x 81 (mm)
     */
    C8("C8", 57, 81),
    /**
     * DL : 110 x 220 (mm)
     */
    DL("DL", 110, 220),
    /**
     * C7/6 : 81 x 162 (mm)
     */
    C7_SLASH_6("C7_SLASH_6", 81, 162);
    
    /**
     * 名称
     */
    private String type;

    /**
     * 宽度
     */
    private int width;

    /**
     * 高度
     */
    private int height;

    /**
     * @param type
     * @param width
     * @param height
     */
    private PageType(String type, int width, int height) {
        this.type = type;
        this.width = width;
        this.height = height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
}
