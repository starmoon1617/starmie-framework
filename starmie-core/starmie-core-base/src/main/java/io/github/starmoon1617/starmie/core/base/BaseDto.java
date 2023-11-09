/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.base;

import java.io.Serializable;

/**
 * 接口/服务返回的结果DTO
 * Base DTO Class for interface/service
 * 
 * @date 2023-10-08
 * @author Nathan Liao
 */
public class BaseDto<D extends Serializable> extends BaseResult {

    private static final long serialVersionUID = 6434448563127660960L;

    /**
     * Generic data 
     */
    private D data;

    /**
     * @return the data
     */
    public D getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(D data) {
        this.data = data;
    }

}
