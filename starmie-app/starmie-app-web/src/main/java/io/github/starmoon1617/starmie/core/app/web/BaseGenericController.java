/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.app.web;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.starmoon1617.starmie.core.app.base.BaseController;
import io.github.starmoon1617.starmie.core.base.BaseDto;
import io.github.starmoon1617.starmie.core.base.BaseEntity;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;
import io.github.starmoon1617.starmie.core.manager.BaseManager;
import io.github.starmoon1617.starmie.core.page.Pagination;
import io.github.starmoon1617.starmie.core.util.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Generic Controller for JSON API Provides RESTful JSON API for CRUD operations
 * and batch import/export
 * 
 * @date 2023-10-23
 * @author Nathan Liao
 */
public abstract class BaseGenericController<E extends BaseEntity<ID, U>, ID extends Serializable, U extends Serializable>
        extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseGenericController.class);

    /**
     * return Manager for subclass
     * 
     * @return
     */
    protected abstract BaseManager<E, ID, U> getManager();

    /**
     * return count for query
     * 
     * @param criteria
     * @return
     */
    protected int count(BaseCriteria criteria) {
        return getManager().count(criteria);
    }

    /**
     * return list for query
     * 
     * @param criteria
     * @return
     */
    protected List<E> find(BaseCriteria criteria) {
        return getManager().find(criteria);
    }

    protected E find(E e) {
        return getManager().find(e);
    }

    /**
     * save batch data
     * 
     * @param datas
     */
    protected void batchSave(List<E> datas) {
        getManager().save(datas);
    }

    /**
     * save single entity
     * 
     * @param e
     */
    protected void doSave(E e) {
        getManager().save(e);
    }

    /**
     * update single entity
     * 
     * @param e
     */
    protected void doUpdate(E e) {
        getManager().update(e);
    }

    /**
     * delete single entity
     * 
     * @param e
     */
    protected void doDelete(E e) {
        getManager().delete(e);
    }

    /**
     * find with pagination
     * 
     * @param pagination
     * @param criteria
     */
    protected void findWithPagination(Pagination<E> pagination, BaseCriteria criteria) {
        getManager().find(pagination, criteria);
    }

    /**
     * get data list with pagination
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/list")
    public BaseDto<Pagination<E>> list(HttpServletRequest request) {
        Pagination<E> pagination = new Pagination<E>();
        findWithPagination(pagination, getCriteria(request));
        return getSuccess(pagination);
    }

    /**
     * get data detail
     * 
     * @param e
     * @return
     */
    @ResponseBody
    @PostMapping("/detail")
    public BaseDto<E> detail(@RequestBody E e) {
        if (e == null) {
            return getFailure(-1, "Data Not Found!");
        }
        E detail = find(e);
        if (detail == null) {
            return getFailure(-1, "Data Not Found!");
        }
        return getSuccess(detail);
    }

    /**
     * save a data
     * 
     * @param e
     * @return
     */
    @ResponseBody
    @PostMapping("/save")
    public BaseDto<E> save(@RequestBody E e) {
        if (e == null) {
            return getFailure(-1, "Data is null!");
        }
        doSave(e);
        return getSuccess(e);
    }

    /**
     * update a data
     * 
     * @param e
     * @return
     */
    @ResponseBody
    @PostMapping("/update")
    public BaseDto<E> update(@RequestBody E e) {
        if (e == null) {
            return getFailure(-1, "Data Not Found!");
        }
        doUpdate(e);
        return getSuccess(e);
    }

    /**
     * delete a data
     * 
     * @param e
     * @return
     */
    @ResponseBody
    @PostMapping("/delete")
    public BaseDto<E> delete(@RequestBody E e) {
        if (e == null) {
            return getFailure(-1, "Data Not Found!");
        }
        doDelete(e);
        return getSuccess(e);
    }

    /**
     * JSON batch import
     * 
     * @param datas - list of entities to import
     * @return
     */
    @ResponseBody
    @PostMapping("/batchImport")
    public BaseDto<String> batchImport(@RequestBody List<E> datas) {
        String errorMsg = null;
        try {
            if (CommonUtils.isEmpty(datas)) {
                return getSuccess("Success! No data to import.");
            }
            errorMsg = validateImportDatas(datas);
            if (!CommonUtils.isNotBlank(errorMsg)) {
                batchSave(datas);
            }
        } catch (Exception e) {
            LOGGER.error("JSON import Error : ", e);
            errorMsg = String.format("JSON import Error: %s", e.getMessage());
        }
        if (CommonUtils.isNotBlank(errorMsg)) {
            return getFailure(-1, errorMsg);
        }
        return getSuccess("Success!");
    }

    /**
     * JSON batch export
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/batchExport")
    public BaseDto<Pagination<E>> jsonExport(HttpServletRequest request) {
        Pagination<E> pagination = new Pagination<E>();
        findWithPagination(pagination, getCriteria(request));
        return getSuccess(pagination);
    }

    /**
     * validate import datas
     * 
     * @param datas
     * @return error message if validation fails, null if success
     */
    protected String validateImportDatas(List<E> datas) {
        return null;
    }

    /**
     * To return the Type of Entity
     * 
     * @return
     */
    protected Type getEntityType() {
        Type superClass = getClass().getGenericSuperclass();
        while (superClass != null) {
            if (superClass instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) superClass;
                Type rawType = paramType.getRawType();
                if (rawType == BaseGenericController.class || (rawType instanceof Class
                        && BaseGenericController.class.isAssignableFrom((Class<?>) rawType))) {
                    return paramType.getActualTypeArguments()[0];
                }
            }
            if (superClass instanceof Class) {
                superClass = ((Class<?>) superClass).getGenericSuperclass();
            } else {
                break;
            }
        }
        return Object.class;
    }
}
