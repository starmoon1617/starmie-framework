/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.base;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础类,定义创建,更新字段 <br>
 * Base Class, define create and update fields.
 * 
 * @date 2023-10-08
 * @author Nathan Liao
 */
public class Base<U extends Serializable> implements Serializable {

    private static final long serialVersionUID = 2823192097357171296L;

    /**
     * 创建时间 
     * create time
     */
    private Date createTime;

    /**
     * 更新时间
     * update time
     * 
     */
    private Date updateTime;

    /**
     * 创建用户
     * create user
     */
    private U createBy;

    /**
     * 更新用户
     * update user
     */
    private U updateBy;

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     *            the createTime to set
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the updateTime
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     *            the updateTime to set
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the createBy
     */
    public U getCreateBy() {
        return createBy;
    }

    /**
     * @param createBy
     *            the createBy to set
     */
    public void setCreateBy(U createBy) {
        this.createBy = createBy;
    }

    /**
     * @return the updateBy
     */
    public U getUpdateBy() {
        return updateBy;
    }

    /**
     * @param updateBy
     *            the updateBy to set
     */
    public void setUpdateBy(U updateBy) {
        this.updateBy = updateBy;
    }
}
