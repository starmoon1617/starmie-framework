/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.service;

import java.io.Serializable;
import java.util.List;

import io.github.starmoon1617.starmie.core.base.BaseEntity;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;
import io.github.starmoon1617.starmie.core.dao.BaseMapper;

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
    protected abstract BaseMapper<E, ID, U> getDao();

    @Override
    public E find(E e) {
        return getDao().select(e);
    }

    @Override
    public int save(E e) {
        return getDao().insert(e);
    }

    @Override
    public int delete(E e) {
        return getDao().delete(e);
    }

    @Override
    public int update(E e) {
        return getDao().update(e);
    }

    @Override
    public List<E> find(BaseCriteria criteria) {
        return getDao().selectList(criteria);
    }

    @Override
    public int count(BaseCriteria criteria) {
        return getDao().count(criteria);
    }

}
