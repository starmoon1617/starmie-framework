/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.criterion;

/**
 * Sort term, type可用类型为 ASC, DESC
 * <br>
 * type in {@code SortType}
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public final class SortCriterion extends Criterion {
    /**
     * 顺序,用于排序
     */
    private int order;

    /**
     * 只有此包可以创建
     */
    SortCriterion() {
    }

    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * @param order
     *            the order to set
     */
    void setOrder(int order) {
        this.order = order;
    }
}
