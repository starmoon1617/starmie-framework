/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.doc.head;

import java.io.Serializable;

import io.github.starmoon1617.starmie.utils.doc.convert.Converter;

/**
 * Head define for doc 表头
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class DocHead implements Serializable {

    private static final long serialVersionUID = -779379819894063801L;

    /**
     * column title
     */
    private String title;

    /**
     * column width
     */
    private Integer width;

    /**
     * field name
     */
    private String field;

    /**
     * field converter
     * 
     */
    private Converter<?> converter;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the converter
     */
    public Converter<?> getConverter() {
        return converter;
    }

    /**
     * @param converter the converter to set
     */
    public void setConverter(Converter<?> converter) {
        this.converter = converter;
    }

}
