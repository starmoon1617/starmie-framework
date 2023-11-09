/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.criterion.enums;

/**
 * Operator Type enums
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public enum OperatorType {
    /**
     * like, 前后自动加'%X%'
     */
    LK("LIKE", "LK"),
    /**
     * like, 右边自动加_, 'X_'
     */
    RLKO("LIKE", "RLKO"),
    /**
     * like, 左边自动加_, '_X'
     */
    LLKO("LIKE", "LLKO"),
    /**
     * like, 前后边自动加_, '_X_'
     */
    SLKO("LIKE", "SLKO"),
    /**
     * like, 右边模糊匹配%, 'X%'
     */
    RLKM("LIKE", "RLKM"),
    /**
     * like, 右边模糊匹配%, '%X'
     */
    LLKM("LIKE", "LLKM"),
    /**
     * 等于
     */
    EQ("=", "EQ"),
    /**
     * 不等于
     */
    NEQ("!=", "NEQ"),
    /**
     * 大于
     */
    GT(">", "GT"),
    /**
     * 大于等于
     */
    GTE(">=", "GTE"),
    /**
     * 小于
     */
    LT("<", "LT"),
    /**
     * 小于等于
     */
    LTE("<=", "LTE"),
    /**
     * in
     */
    IN("IN", "IN"),
    /**
     * not in
     */
    NIN("NOT IN", "NIN"),
    /**
     * is null
     */
    ISN("IS NULL", "ISN"),
    /**
     * is not null
     */
    ISNN("IS NOT NULL", "ISNN"),
    /**
     * between
     */
    BTW("BETWEEN", "BTW");

    /**
     * 操作
     */
    private String opreator;

    /**
     * 名称
     */
    private String type;

    /**
     * @param opreator
     */
    private OperatorType(String opreator, String type) {
        this.opreator = opreator;
        this.type = type;
    }

    /**
     * 返回操作符
     * 
     * @return
     */
    public String opreator() {
        return opreator;
    }

    /**
     * 返回类型
     * 
     * @return
     */
    public String type() {
        return type;
    }
}
