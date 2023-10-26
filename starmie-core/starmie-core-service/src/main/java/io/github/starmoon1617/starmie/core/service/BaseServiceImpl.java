/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.service;

import java.io.Serializable;
import java.util.List;

import io.github.starmoon1617.starmie.core.base.BaseEntity;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;
import io.github.starmoon1617.starmie.core.mapper.BaseMapper;

/**
 * base implementation for service interface
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public abstract class BaseServiceImpl<E extends BaseEntity<ID, U>, ID extends Serializable, U extends Serializable> implements BaseService<E, ID, U> {

    /**
     * return data access for subclass
     * 
     * @return
     */
    protected abstract BaseMapper<E, ID, U> getMapper();

    @Override
    public E find(E e) {
        return getMapper().select(e);
    }

    @Override
    public int save(E e) {
        return getMapper().insert(e);
    }

    @Override
    public int delete(E e) {
        return getMapper().delete(e);
    }

    @Override
    public int update(E e) {
        return getMapper().update(e);
    }

    @Override
    public List<E> find(BaseCriteria criteria) {
        return getMapper().selectList(criteria);
    }

    @Override
    public int count(BaseCriteria criteria) {
        return getMapper().count(criteria);
    }

}
