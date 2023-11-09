/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.app.web;

import java.io.Serializable;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.starmoon1617.starmie.core.base.BaseDto;
import io.github.starmoon1617.starmie.core.base.BaseEntity;
import io.github.starmoon1617.starmie.core.constant.InterpunctionConstants;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;
import io.github.starmoon1617.starmie.core.manager.BaseManager;
import io.github.starmoon1617.starmie.core.page.Pagination;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Generic Controller for Entity
 * 
 * @date 2023-10-23
 * @author Nathan Liao
 */
public abstract class AbstractBaseController<E extends BaseEntity<ID, U>, ID extends Serializable, U extends Serializable> extends BaseGenericController<E> {

    /**
     * return Manager for subclass
     * 
     * @return
     */
    protected abstract BaseManager<E, ID, U> getManager();

    /**
     * return path for view
     * 
     * @return
     */
    protected abstract String getPath();

    @Override
    protected int count(BaseCriteria criteria) {
        return getManager().count(criteria);
    }

    @Override
    protected List<E> find(BaseCriteria criteria) {
        return getManager().find(criteria);
    }

    @Override
    protected void batchSave(List<E> datas) {
        getManager().save(datas);
    }

    /**
     * Go to list page
     * 
     * @return
     */
    @RequestMapping(value = "/toList", method = RequestMethod.GET)
    public String toList() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPath()).append(InterpunctionConstants.SLASH).append("list");
        return sb.toString();
    }

    /**
     * Go to add page
     * 
     * @return
     */
    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public String toAdd() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPath()).append(InterpunctionConstants.SLASH).append("add");
        return sb.toString();
    }

    /**
     * Go to edit page
     * 
     * @param e
     * @param model
     * @return
     */
    @RequestMapping(value = "/toEdit")
    public String toEdit(E e, Model model) {
        model.addAttribute("entity", getManager().find(e));
        StringBuilder sb = new StringBuilder();
        sb.append(getPath()).append(InterpunctionConstants.SLASH).append("edit");
        return sb.toString();
    }

    /**
     * Go to delete page
     * 
     * @param e
     * @param model
     * @return
     */
    @RequestMapping(value = "/toDelete")
    public String toDelete(E e, Model model) {
        model.addAttribute("entity", getManager().find(e));
        StringBuilder sb = new StringBuilder();
        sb.append(getPath()).append(InterpunctionConstants.SLASH).append("delete");
        return sb.toString();
    }

    /**
     * get data list
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseDto<Pagination<E>> list(HttpServletRequest request) {
        Pagination<E> pagination = new Pagination<E>();
        getManager().find(pagination, getCriteria(request));
        return getSuccess(pagination);
    }

    /**
     * add a data
     * 
     * @param e
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public BaseDto<E> save(E e) {
        if (e != null) {
            getManager().save(e);
        }
        return getSuccess(e);
    }

    /**
     * delete a data
     * 
     * @param e
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseDto<E> update(E e) {
        if (e != null) {
            getManager().update(e);
            return getSuccess(e);
        }
        return getFailure(-1, "Data Not Found!");
    }

    /**
     * delete a data
     * 
     * @param e
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseDto<E> delete(E e) {
        if (e != null) {
            getManager().delete(e);
            return getSuccess(e);
        }
        return getFailure(-1, "Data Not Found!");
    }

}
