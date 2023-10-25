/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.app.enums;

/**
 * Mime type
 * 
 * @date 2023-10-23
 * @author Nathan Liao
 */
public enum MimeType {

    /**
     * XLSX - application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
     */
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx", "EXCEL"),

    /**
     * PDF - application/pdf
     */
    PDF("application/pdf", ".pdf", "PDF");

    private String type;

    private String ext;

    private String desc;

    /**
     * @param type
     * @param ext
     * @param desc
     */
    private MimeType(String type, String ext, String desc) {
        this.type = type;
        this.ext = ext;
        this.desc = desc;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the ext
     */
    public String getExt() {
        return ext;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

}
