/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.service;

import java.io.Serializable;
import java.util.List;

import io.github.starmoon1617.starmie.core.base.BaseEntity;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;

/**
 * interface for service
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public interface BaseService<E extends BaseEntity<ID, U>, ID extends Serializable, U extends Serializable> {
    /**
     * 查找
     * 
     * @param e
     * @return
     */
    E find(E e);

    /**
     * 保存单个数据
     * 
     * @param e
     */
    int save(E e);

    /**
     * 删除单个数据
     * 
     * @param e
     */
    int delete(E e);

    /**
     * 更新单个数据
     * 
     * @param e
     */
    int update(E e);

    /**
     * 查询数据
     * 
     * @param criteria
     * @return
     */
    List<E> find(BaseCriteria criteria);

    /**
     * 计数
     * 
     * @param criteria
     * @return
     */
    int count(BaseCriteria criteria);
}
