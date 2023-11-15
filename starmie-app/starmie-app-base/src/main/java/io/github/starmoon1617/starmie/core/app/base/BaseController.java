/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.app.base;

import io.github.starmoon1617.starmie.core.app.util.CriteriaUtils;
import io.github.starmoon1617.starmie.core.base.BaseDto;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Base Class for Controller
 * 
 * @date 2023-10-23
 * @author Nathan Liao
 */
public class BaseController {

    /**
     * get result
     * 
     * @param <D>
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public <D> BaseDto<D> getResult(int code, String msg, D data) {
        BaseDto<D> dto = new BaseDto<>();
        dto.setCode(0);
        dto.setMsg(msg);
        dto.setData(data);
        return dto;
    }

    /**
     * get Success result, code 0
     * 
     * @param <D>
     * @param data
     * @return
     */
    public <D> BaseDto<D> getSuccess(D data) {
        return getResult(0, null, data);
    }

    /**
     * @param <D>
     * @param code
     * @param msg
     * @return
     */
    public <D> BaseDto<D> getFailure(int code, String msg) {
        return getResult(code, msg, null);
    }

    /**
     * get BaseCriteria
     * 
     * @return
     */
    protected BaseCriteria getCriteria() {
        return CriteriaUtils.getCriteria();
    }

    /**
     * get BaseCriteria
     * 
     * @param request
     * @return
     */
    protected BaseCriteria getCriteria(HttpServletRequest request) {
        return CriteriaUtils.getCriteria(request);
    }

}
