/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.criterion;

import java.util.Comparator;

/**
 * Comparator for Sort term
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public class SortCriterionComparator implements Comparator<SortCriterion> {

    @Override
    public int compare(SortCriterion o1, SortCriterion o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1 == null) {
            return -1;
        }
        if (o1.getOrder() == o2.getOrder()) {
            return 0;
        }
        return (o1.getOrder() < o2.getOrder() ? -1 : 1);
    }

}
