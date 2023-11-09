/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.criterion.enums;

/**
 * Combina Type enums
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public enum CombinaType {
    /**
     * 非组合
     */
    NON_COMBINA(false, ""),
    /**
     * 非组合AND
     */
    NON_COMBINA_AND(false, "AND"),
    /**
     * 非组合OR
     */
    NON_COMBINA_OR(false, "OR"),

    /**
     * 组合,外部AND
     */
    COMBINA_OUTER_AND(true, "AND"),
    /**
     * 组合,外部OR
     */
    COMBINA_OUTER_OR(true, "OR"),
    /**
     * 组合AND
     */
    COMBINA_AND(true, "AND"),
    /**
     * 组合OR
     */
    COMBINA_OR(true, "OR"),
    /**
     * 组合
     */
    COMBINA(true, "");

    /**
     * 是否组合条件
     */
    private boolean combina;

    /**
     * 组合的类型
     */
    private String joinType;

    /**
     * @param combina
     * @param joinType
     */
    private CombinaType(boolean combina, String joinType) {
        this.combina = combina;
        this.joinType = joinType;
    }

    /**
     * 返回组合类型
     * 
     * @return
     */
    public boolean combina() {
        return combina;
    }

    /**
     * 连接类型
     * 
     * @return
     */
    public String joinType() {
        return joinType;
    }
}
