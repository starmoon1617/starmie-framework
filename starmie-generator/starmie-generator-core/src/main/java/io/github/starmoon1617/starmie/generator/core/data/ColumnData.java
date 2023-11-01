/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.data;

import java.io.Serializable;

/**
 * data for a XML column
 * 
 * @date 2023-10-11
 * @author Nathan Liao
 */
public class ColumnData implements Serializable {

    private static final long serialVersionUID = 9099274326113512758L;

    /**
     * column name
     */
    private String column;

    /**
     * java field name
     */
    private String property;

    /**
     * String JDBC Type name
     */
    private String jdbcType;

    /**
     * Java Type
     */
    private String javaType;

    /**
     * length
     */
    private int length;

    /**
     * null able
     */
    private String nullAble;

    /**
     * remark
     */
    private String remark;

    /**
     * @return the column
     */
    public String getColumn() {
        return column;
    }

    /**
     * @param column
     *            the column to set
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /**
     * @param property
     *            the property to set
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * @return the jdbcType
     */
    public String getJdbcType() {
        return jdbcType;
    }

    /**
     * @param jdbcType
     *            the jdbcType to set
     */
    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    /**
     * @return the javaType
     */
    public String getJavaType() {
        return javaType;
    }

    /**
     * @param javaType
     *            the javaType to set
     */
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length
     *            the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the nullAble
     */
    public String getNullAble() {
        return nullAble;
    }

    /**
     * @param nullAble
     *            the nullAble to set
     */
    public void setNullAble(String nullAble) {
        this.nullAble = nullAble;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     *            the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
