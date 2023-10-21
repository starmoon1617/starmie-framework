/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.data;

/**
 * Data for XML file
 * 
 * @date 2023-10-11
 * @author Nathan Liao
 */
public class XmlData extends TextData {

    private static final long serialVersionUID = 5092129029879459639L;

    /**
     * XML name space class
     */
    private String className;

    /**
     * XML model class name
     */
    private String modelName;

    /**
     * result map id name
     */
    private String resultMapId;

    /**
     * column sql id
     */
    private String columnSqlId;

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className
     *            the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the modelName
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @param modelName
     *            the modelName to set
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @return the resultMapId
     */
    public String getResultMapId() {
        return resultMapId;
    }

    /**
     * @param resultMapId
     *            the resultMapId to set
     */
    public void setResultMapId(String resultMapId) {
        this.resultMapId = resultMapId;
    }

    /**
     * @return the columnSqlId
     */
    public String getColumnSqlId() {
        return columnSqlId;
    }

    /**
     * @param columnSqlId
     *            the columnSqlId to set
     */
    public void setColumnSqlId(String columnSqlId) {
        this.columnSqlId = columnSqlId;
    }
    
}
