/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.criterion;

/**
 * Conditions term
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public class CondCriteria extends CondCriterion {
    /**
     * 是否复合条件, 是则需要使用括号()
     */
    private boolean combina;

    /**
     * @return the combina
     */
    public boolean isCombina() {
        return combina;
    }

    /**
     * @param combina the combina to set
     */
    public void setCombina(boolean combina) {
        this.combina = combina;
    }
}
