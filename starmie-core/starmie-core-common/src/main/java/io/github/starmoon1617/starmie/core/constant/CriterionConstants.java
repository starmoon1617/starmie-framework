/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.constant;

/**
 * Constants for Criterion 
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public class CriterionConstants {

    private CriterionConstants() {
    }

    /**
     * 没有值的条件, (not)null
     */
    public static final String TYPE_NONE = "NONE";

    /**
     * 一个值的条件
     */
    public static final String TYPE_SINGLE = "SINGLE";

    /**
     * 两个值的条件, between
     */
    public static final String TYPE_DUAL = "DUAL";

    /**
     * 多个值的条件, (not)in
     */
    public static final String TYPE_MULTI = "MULTI";

    /**
     * like
     */
    public static final String OPER_LIKE = "LIKE";

    /**
     * 默认的分页数量 - 20
     */
    public static final int PAGESIZE = 20;

    /**
     * 默认的开始位置 - 0
     */
    public static final int OFFSET = 0;

}
