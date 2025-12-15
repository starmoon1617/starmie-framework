/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.criterion;

import io.github.starmoon1617.starmie.core.util.EntityUtils;

/**
 * base term Class
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public abstract class Criterion {

    /**
     * type
     */
    private String type;

    /**
     * 条件
     */
    private String term;

    /**
     * 此包可以创建
     */
    Criterion() {
    }
    
    
    /**
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * @param term
     *            the term to set
     */
    void setTerm(String term) {
        this.term = term;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return EntityUtils.toNonNJson(this);
    }
}
