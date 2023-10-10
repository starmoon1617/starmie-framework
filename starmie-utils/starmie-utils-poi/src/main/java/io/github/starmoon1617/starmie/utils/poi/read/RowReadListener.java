/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.poi.read;

/**
 * listener for one row read
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public interface RowReadListener<T> {

    /**
     * process data after one row read
     * 
     * @param rowNum
     * @param t
     */
    void postHandle(int rowNum, T t);
}
