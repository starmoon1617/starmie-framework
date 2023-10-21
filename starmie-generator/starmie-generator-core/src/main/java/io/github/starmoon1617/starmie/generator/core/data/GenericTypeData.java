/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.data;

import java.io.Serializable;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

/**
 * data for Generic
 * 
 * @date 2023-10-19
 * @author Nathan Liao
 */
public class GenericTypeData implements Serializable {

    private static final long serialVersionUID = 8610126322708553936L;

    /**
     * Generic type
     */
    private String typeName;

    /**
     * java type
     */
    private FullyQualifiedJavaType javaType;

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName
     *            the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the javaType
     */
    public FullyQualifiedJavaType getJavaType() {
        return javaType;
    }

    /**
     * @param javaType
     *            the javaType to set
     */
    public void setJavaType(FullyQualifiedJavaType javaType) {
        this.javaType = javaType;
    }

}
