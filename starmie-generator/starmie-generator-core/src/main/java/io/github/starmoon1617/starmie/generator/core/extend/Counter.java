/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 * 
 */
package io.github.starmoon1617.starmie.generator.core.extend;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.mybatis.generator.internal.util.StringUtility;

/**
 * Counter Help to do count
 * 
 * @date 2023-11-18
 * @author Nathan Liao
 */
public class Counter {

    private Map<String, AtomicInteger> counts = new LinkedHashMap<>();

    /**
     * set name to the count
     * 
     * @param name
     * @param count
     */
    public void set(String name, int count) {
        if (!StringUtility.stringHasValue(name)) {
            return;
        }
        counts.computeIfAbsent(name, k -> new AtomicInteger()).set(count);
    }

    /**
     * get the count of the name
     * 
     * @param name
     * @return
     */
    public int get(String name) {
        if (!StringUtility.stringHasValue(name)) {
            return 0;
        }
        AtomicInteger ai = counts.get(name);
        return (ai != null ? ai.get() : 0);
    }

}
