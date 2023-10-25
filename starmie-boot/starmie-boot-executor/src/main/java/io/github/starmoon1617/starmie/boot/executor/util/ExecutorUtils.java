/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.boot.executor.util;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * define for property key and default value
 * 
 * @date 2023-10-25
 * @author Nathan Liao
 */
public class ExecutorUtils {

    private ExecutorUtils() {

    }

    /**
     * name suffix for executor bean, executor name is 'xxxExor'
     */
    private static final String BEAN_NAME_SUFFIX = "Exor";

    /**
     * name suffix for thread name, executor thread name is 'xxx-exor'
     */
    private static final String THREAD_NAME_SUFFIX = "-exor";

    /**
     * for series executor
     */
    private static final String TILDE = "~";

    /**
     * prefix for key
     */
    public static final String PROPERTY_PREFIX = "starmie.executor.";

    /**
     * names
     */
    public static final String NAMES = "names";

    /**
     * default key, for all executors
     */
    public static final String KEY_DEFAULT = "default";

    /**
     * core size
     */
    public static final String CORE_SIZE = ".coreSize";

    /**
     * max size
     */
    public static final String MAX_SIZE = ".maxSize";

    /**
     * queue size
     */
    public static final String CAPACITY = ".capacity";

    /**
     * alive Seconds
     */
    public static final String ALIVE_SECONDS = ".aliveSeconds";

    /**
     * await Termination Seconds
     */
    public static final String AWAIT_TERMINATION_SECONDS = ".awaitTerminationSeconds";

    /**
     * allow Core Thread TimeOut
     */
    public static final String ALLOW_CORE_THREAD_TIMEOUT = ".allowCoreThreadTimeOut";

    /**
     * graceful shutdown
     */
    public static final String WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = ".waitForTasksToCompleteOnShutdown";

    /**
     * rejected handler, bean name
     */
    public static final String REJECTED_HANDLER = ".rejectedHandler";

    /**
     * task decorator, bean name
     */
    public static final String TASK_DECORATOR = ".taskDecorator";

    /**
     * thread Factory, bean name
     */
    public static final String THREAD_FACTORY = ".threadFactory";

    public static final int getCoreSize(Environment env, String name, int defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + CORE_SIZE, Integer.class, defaultValue);
    }

    public static final int getMaxSize(Environment env, String name, int defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + MAX_SIZE, Integer.class, defaultValue);
    }

    public static final int getCapacity(Environment env, String name, int defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + CAPACITY, Integer.class, defaultValue);
    }

    public static final int getAliveSeconds(Environment env, String name, int defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + ALIVE_SECONDS, Integer.class, defaultValue);
    }

    public static final int getAwaitTerminationSeconds(Environment env, String name, int defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + AWAIT_TERMINATION_SECONDS, Integer.class, defaultValue);
    }

    public static final boolean getAllowCoreThreadTimeout(Environment env, String name, boolean defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + ALLOW_CORE_THREAD_TIMEOUT, Boolean.class, defaultValue);
    }

    public static final boolean getWaitForTasksToCompleteOnShutdown(Environment env, String name, boolean defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN, Boolean.class, defaultValue);
    }

    public static final String getRejectedHandler(Environment env, String name, String defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + REJECTED_HANDLER, String.class, defaultValue);
    }

    public static final String getTaskDecorator(Environment env, String name, String defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + TASK_DECORATOR, String.class, defaultValue);
    }

    public static final String getThreadFactory(Environment env, String name, String defaultValue) {
        return env.getProperty(PROPERTY_PREFIX + name + THREAD_FACTORY, String.class, defaultValue);
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
