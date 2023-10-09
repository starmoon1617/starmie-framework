/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.base;

import java.io.Serializable;

/**
 * 接口/服务返回的结果类
 * Result Class for interface/service
 * 
 * @date 2023-10-08
 * @author Nathan Liao
 */
public class BaseResult implements Serializable {

    private static final long serialVersionUID = 5347846959038933042L;

    /**
     * result code
     */
    private int code;

    /**
     * result message
     */
    private String msg;

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg
     *            the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

}
