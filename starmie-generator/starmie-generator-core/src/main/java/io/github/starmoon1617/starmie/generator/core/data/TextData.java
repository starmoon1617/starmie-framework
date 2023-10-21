/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.data;

import java.io.Serializable;
import java.util.List;

/**
 * base data for text file (JavaScript, View, XML)
 * 
 * @date 2023-10-11
 * @author Nathan Liao
 */
public class TextData implements Serializable {

    private static final long serialVersionUID = -7207242993062654405L;

    /**
     * comment for text
     */
    private String comment;

    /**
     * file name
     */
    private String name;

    /**
     * table name
     */
    private String tableName;

    /**
     * column list
     */
    private List<ColumnData> columns;

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName
     *            the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the columns
     */
    public List<ColumnData> getColumns() {
        return columns;
    }

    /**
     * @param columns
     *            the columns to set
     */
    public void setColumns(List<ColumnData> columns) {
        this.columns = columns;
    }

}
