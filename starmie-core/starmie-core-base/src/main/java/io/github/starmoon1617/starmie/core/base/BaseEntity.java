/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.base;

import java.io.Serializable;

/**
 * 基础实体类,定义ID
 * Base Entity Class , define ID
 * 
 * @date 2023-10-08
 * @author Nathan Liao
 */
public class BaseEntity<ID extends Serializable, U extends Serializable> extends Base<U> {

    private static final long serialVersionUID = -5338344785167622695L;

    /**
     * id
     */
    private ID id;

    /**
     * @return the id
     */
    public ID getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(ID id) {
        this.id = id;
    }

}
