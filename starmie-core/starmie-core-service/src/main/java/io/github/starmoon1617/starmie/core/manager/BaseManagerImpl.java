/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.starmoon1617.starmie.core.base.BaseEntity;
import io.github.starmoon1617.starmie.core.constant.CriterionConstants;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;
import io.github.starmoon1617.starmie.core.criterion.enums.LimitationType;
import io.github.starmoon1617.starmie.core.page.Pagination;
import io.github.starmoon1617.starmie.core.service.BaseService;
import io.github.starmoon1617.starmie.core.util.CommonUtils;

/**
 * base implementation for manager interface
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public abstract class BaseManagerImpl<E extends BaseEntity<ID, U>, ID extends Serializable, U extends Serializable> implements BaseManager<E, ID, U> {

    /**
     * return Service for subclass
     * 
     * @return
     */
    protected abstract BaseService<E, ID, U> getService();

    @Override
    public E find(E e) {
        return getService().find(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(E e) {
        return getService().save(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(Collection<E> es) {
        if (CommonUtils.isEmpty(es)) {
            return 0;
        }
        for (E e : es) {
            getService().save(e);
        }
        return es.size();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int delete(E e) {
        return getService().delete(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int update(E e) {
        return getService().update(e);
    }

    @Override
    public List<E> find(BaseCriteria criteria) {
        return getService().find(criteria);
    }

    @Override
    public int count(BaseCriteria criteria) {
        return getService().count(criteria);
    }

    @Override
    public void find(Pagination<E> pagination, BaseCriteria criteria) {
        calculateLimitation(pagination, criteria);
        int pageSize = pagination.getPageSize();
        int count = getService().count(criteria);
        pagination.setTotal(count);
        int mod = count % pageSize;
        pagination.setTotalPage((count - mod) / pageSize + (mod > 0 ? 1 : 0));
        if (count > 0) {
            if (pagination.getPageNo() >= pagination.getTotalPage()) {
                pagination.setPageNo(CriterionConstants.OFFSET);
                updateLimitation(criteria, pagination.getPageNo(), pageSize);
            }
            List<E> es = getService().find(criteria);
            if (!CommonUtils.isEmpty(es)) {
                pagination.setElms(es);
                pagination.setSize(es.size());
            }
        } else {
            pagination.setPageNo(CriterionConstants.OFFSET);
        }
    }

    /**
     * 计算查询数量限制.优先级为pagination -> criteria -> default
     * 
     * @param pagination
     * @param criteria
     */
    protected void calculateLimitation(Pagination<E> pagination, BaseCriteria criteria) {
        int pageSize = CriterionConstants.PAGESIZE;
        int currentPage = CriterionConstants.OFFSET;
        if (criteria == null) {
            criteria = BaseCriteria.getInstance();
        }
        if (pagination != null) {
            if (pagination.getPageSize() != 0) {
                pageSize = pagination.getPageSize();
            } else if (criteria.getLimit() != null) {
                pageSize = criteria.getLimit().intValue();
            }
            if (pagination.getPageNo() != 0) {
                currentPage = pagination.getPageNo();
            } else if (criteria.getOffset() != null) {
                currentPage = criteria.getOffset().intValue() / pageSize;
            }
        } else {
            pagination = new Pagination<>();
            if (criteria.getLimit() != null && pageSize == CriterionConstants.PAGESIZE) {
                pageSize = criteria.getLimit().intValue();
            }
            if (criteria.getOffset() != null) {
                currentPage = criteria.getOffset().intValue() / pageSize;
            }
        }
        pagination.setPageNo(currentPage);
        pagination.setPageSize(pageSize);
        updateLimitation(criteria, currentPage, pageSize);
    }

    /**
     * 根据传入的当前页和分页数更新criteria的查询数量限制
     * 
     * @param criteria
     * @param currentPage
     * @param pageSize
     */
    protected void updateLimitation(BaseCriteria criteria, int currentPage, int pageSize) {
        criteria.addLimitation(LimitationType.LIMIT, pageSize);
        criteria.addLimitation(LimitationType.OFFSET, currentPage * pageSize);
        criteria.addLimitation(LimitationType.END, (currentPage + 1) * pageSize);
    }
}
