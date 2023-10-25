/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import io.github.starmoon1617.starmie.core.base.BaseEntity;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;
import io.github.starmoon1617.starmie.core.page.Pagination;

/**
 * base interface for manager
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public interface BaseManager<E extends BaseEntity<ID, U>, ID extends Serializable, U extends Serializable> {
    /**
     * 查找
     * 
     * @param id
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
     * 保存多个数据
     * 
     * @param es
     */
    int save(Collection<E> es);

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
     * 分页查找, 查询的数据设置到pagination对象
     * 
     * @param pagination
     *            - 分页对象
     * @param criteria
     *            - 查询条件
     * @return
     */
    void find(final Pagination<E> pagination, final BaseCriteria criteria);

    /**
     * 查询数据
     * 
     * @param criteria
     * @return
     */
    List<E> find(final BaseCriteria criteria);

    /**
     * 计数
     * 
     * @param criteria
     * @return
     */
    int count(final BaseCriteria criteria);
}
