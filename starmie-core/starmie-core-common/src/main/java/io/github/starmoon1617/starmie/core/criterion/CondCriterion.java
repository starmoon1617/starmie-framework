/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.criterion;

import java.util.List;

/**
 * Condition term,  type可用类型为 NONE, SINGLE, DUAL, MULTI
 * <br>
 * type in {@code CriterionConstants}
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public class CondCriterion extends Criterion {
    
    /**
     * 连接方式, AND或是OR
     */
    private String joinType;

    /**
     * 操作</>/=/!= etc...
     */
    private String operator;
    
    /**
     * 值, between为value[0], values[1], in, not in 等使用values, 单值为values[0]
     */
    private List<Object> values;
    
    /**
     * @return the joinType
     */
    public String getJoinType() {
        return joinType;
    }

    /**
     * @param joinType
     *            the joinType to set
     */
    void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator
     *            the operator to set
     */
    void setOperator(String operator) {
        this.operator = operator;
    }
    
    /**
     * @return the values
     */
    public List<Object> getValues() {
        return values;
    }

    /**
     * @param values
     *            the values to set
     */
    void setValues(List<Object> values) {
        this.values = values;
    }

}
