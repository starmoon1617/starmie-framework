/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.criterion.enums;

/**
 * 
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public enum LimitationType {
    /**
     * 开始位置
     */
    OFFSET,
    /**
     * 限制查询的数量
     */
    LIMIT,
    /**
     * 结束位置(OFFSET + LIMIT)
     */
    END;
}
