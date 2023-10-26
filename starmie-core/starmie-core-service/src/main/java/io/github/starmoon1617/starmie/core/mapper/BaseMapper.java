/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.mapper;

import java.io.Serializable;
import java.util.List;

import io.github.starmoon1617.starmie.core.base.BaseEntity;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;

/**
 * interface for data access
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public interface BaseMapper<E extends BaseEntity<ID, U>, ID extends Serializable, U extends Serializable> {
    
    /**
     * 查找
     * 
     * @param e
     * @return
     */
    E select(E e);
    
    /**
     * 插入单条数据
     * 
     * @param e
     */
    int insert(E e);
    
    /**
     * 更新数据,并返回更新的条数
     * 
     * @param e
     */
    int update(E e);

    /**
     * 根据ID删除
     * 
     * @param e
     */
    int delete(E e);

    /**
     * 查询数据
     * 
     * @param criteria
     * @return
     */
    List<E> selectList(BaseCriteria criteria);

    /**
     * 计数
     * 
     * @param criteria
     * @return
     */
    int count(BaseCriteria criteria);
}
