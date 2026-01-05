/*
 * Copyright (c) 2025, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.boot.executor.vt.util;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * define for property key and default value for Virtual Thread Executor
 * 
 * @date 2025-12-20
 * @author Nathan Liao
 */
public class VirtualThreadExecutorUtils {

    private VirtualThreadExecutorUtils() {

    }

    /**
     * name suffix for executor bean, executor name is 'xxxExor'
     */
    private static final String BEAN_NAME_SUFFIX = "Exor";

    /**
     * name suffix for thread name, executor thread name is 'xxx-vt-'
     */
    private static final String THREAD_NAME_SUFFIX = "-vt-";

    /**
     * for series executor
     */
    private static final String TILDE = "~";

    /**
     * prefix for key
     */
    public static final String PROPERTY_PREFIX = "starmie.executor.vt.";

    /**
     * names
     */
    public static final String NAMES = "names";

    /**
     * default key, for all executors
     */
    public static final String KEY_DEFAULT = "default";

    /**
     * task decorator, bean name
     */
    public static final String TASK_DECORATOR = ".taskDecorator";
    
    /**
     * thread name prefix
     */
    public static final String THREAD_NAME_PREFIX = ".threadNamePrefix";

    public static final String getTaskDecorator(Environment env, String name, String defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + TASK_DECORATOR, String.class, defaultValue);
    }
    
    public static final String getThreadNamePrefix(Environment env, String name, String defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + THREAD_NAME_PREFIX, String.class, defaultValue);
    }

    public static final String getExecutorName(String name) {
        return name.concat(BEAN_NAME_SUFFIX);
    }

    public static final String getExecutorThreadName(String name) {
        return name.concat(THREAD_NAME_SUFFIX);
    }

    /**
     * get executor names from env
     * 
     * @param env
     * @return
     */
    public static final Set<String> getNames(Environment env) {
        String nameStr = env.getProperty(PROPERTY_PREFIX + NAMES);
        if (!StringUtils.hasText(nameStr)) {
            return null;
        }
        Set<String> names = new LinkedHashSet<>();
        String[] arr = StringUtils.commaDelimitedListToStringArray(nameStr);
        for (String name : arr) {
            if (!StringUtils.hasText(name)) {
                continue;
            }
            int tildeIndex = name.indexOf(TILDE);
            if (tildeIndex < 0) {
                names.add(name);
                continue;
            }
            // it should be a number after ~
            int number = -1;
            try {
                number = Integer.parseInt(name.substring(tildeIndex + 1));
            } catch (Exception e) {
                number = -1;
            }
            if (number < 0) {
                names.add(name);
                continue;
            }
            String nName = name.substring(0, tildeIndex);
            if (!StringUtils.hasText(nName)) {
                continue;
            }
            for (int i = 0; i < number; i++) {
                names.add(nName + i);
            }
        }
        return names;
    }
}
