/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.boot.executor.factory;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import io.github.starmoon1617.starmie.boot.executor.util.ExecutorUtils;

/**
 * Help to registry ThreadPoolTaskExecutor as bean with configuration
 * starmie.executor.names
 * 
 * @date 2023-10-25
 * @author Nathan Liao
 */
public class ExecutorRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorRegistryPostProcessor.class);

    private ApplicationContext applicationContext;

    /**
     * default core size - 1
     */
    private int coreSize = 1;

    /**
     * default max size - 3
     */
    private int maxSize = 3;

    /**
     * default queue size - 10000
     */
    private int queueCapacity = 10000;

    /**
     * default Alive Seconds - 300 seconds (5 minutes)
     */
    private int aliveSeconds = 300;

    /**
     * default await Termination Seconds - 600 seconds (10 minutes)
     */
    private int awaitTerminationSeconds = 600;

    /**
     * allowCoreThreadTimeOut - default : true (ThreadPoolTaskExecutor : false)
     */
    private boolean allowCoreThreadTimeout = true;

    /**
     * graceful shutdown - default : true (ThreadPoolTaskExecutor : false)
     */
    private boolean waitForTasksToCompleteOnShutdown = true;

    /**
     * 默认的拒绝策略beanName
     */
    private String rejectedHandler = "";

    /**
     * 默认的任务装饰器beanName
     */
    private String taskDecorator = "";

    /**
     * 默认的线程工厂类
     */
    private String threadFactory = "";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initDefaultProperties(applicationContext.getEnvironment());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Environment env = applicationContext.getEnvironment();
        Set<String> names = ExecutorUtils.getNames(env);
        if (names == null || names.isEmpty()) {
            LOGGER.debug("No executor names found by configuration : {}{} ", ExecutorUtils.PROPERTY_PREFIX, ExecutorUtils.NAMES);
            return;
        }
        for (String name : names) {
            registryExecutorBeanDefinition(name, env, registry);
        }
    }

    /**
     * registry Executor Bean
     * 
     * @param name
     * @param env
     */
    protected void registryExecutorBeanDefinition(String name, Environment env, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolTaskExecutor.class);

        String beanName = ExecutorUtils.getExecutorName(name);
        builder.addPropertyValue("beanName", beanName);
        builder.addPropertyValue("threadNamePrefix", ExecutorUtils.getExecutorThreadName(name));

        int cSize = ExecutorUtils.getCoreSize(env, name, coreSize);
        int mSize = ExecutorUtils.getMaxSize(env, name, maxSize);
        int capacity = ExecutorUtils.getCapacity(env, name, queueCapacity);
        builder.addPropertyValue("corePoolSize", cSize);
        builder.addPropertyValue("maxPoolSize", mSize);
        builder.addPropertyValue("queueCapacity", capacity);

        builder.addPropertyValue("keepAliveSeconds", ExecutorUtils.getAliveSeconds(env, name, aliveSeconds));
        builder.addPropertyValue("awaitTerminationSeconds", ExecutorUtils.getAwaitTerminationSeconds(env, name, awaitTerminationSeconds));
        builder.addPropertyValue("allowCoreThreadTimeOut", ExecutorUtils.getAllowCoreThreadTimeout(env, name, allowCoreThreadTimeout));
        builder.addPropertyValue("waitForTasksToCompleteOnShutdown",
                ExecutorUtils.getWaitForTasksToCompleteOnShutdown(env, name, waitForTasksToCompleteOnShutdown));

        String rHandler = ExecutorUtils.getRejectedHandler(env, name, rejectedHandler);
        if (StringUtils.hasText(rHandler)) {
            builder.addPropertyReference("rejectedExecutionHandler", rHandler);
        }

        String decorator = ExecutorUtils.getTaskDecorator(env, name, taskDecorator);
        if (StringUtils.hasText(decorator)) {
            builder.addPropertyReference("taskDecorator", decorator);
        }

        String factory = ExecutorUtils.getThreadFactory(env, name, threadFactory);
        if (StringUtils.hasText(factory)) {
            builder.addPropertyReference("threadFactory", factory);
        }

        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
        LOGGER.debug("Registered Executor(name={}) with (coreSize={},maxSize={},capacity={}).", beanName, cSize, mSize, capacity);
    }

    /**
     * over write default properties
     * 
     * @param env
     */
    protected void initDefaultProperties(Environment env) {
        coreSize = ExecutorUtils.getCoreSize(env, ExecutorUtils.KEY_DEFAULT, coreSize);
        maxSize = ExecutorUtils.getMaxSize(env, ExecutorUtils.KEY_DEFAULT, maxSize);
        queueCapacity = ExecutorUtils.getCapacity(env, ExecutorUtils.KEY_DEFAULT, queueCapacity);
        aliveSeconds = ExecutorUtils.getAliveSeconds(env, ExecutorUtils.KEY_DEFAULT, aliveSeconds);
        awaitTerminationSeconds = ExecutorUtils.getAwaitTerminationSeconds(env, ExecutorUtils.KEY_DEFAULT, awaitTerminationSeconds);
        allowCoreThreadTimeout = ExecutorUtils.getAllowCoreThreadTimeout(env, ExecutorUtils.KEY_DEFAULT, allowCoreThreadTimeout);
        waitForTasksToCompleteOnShutdown = ExecutorUtils.getWaitForTasksToCompleteOnShutdown(env, ExecutorUtils.KEY_DEFAULT, waitForTasksToCompleteOnShutdown);
        rejectedHandler = ExecutorUtils.getRejectedHandler(env, ExecutorUtils.KEY_DEFAULT, rejectedHandler);
        taskDecorator = ExecutorUtils.getTaskDecorator(env, ExecutorUtils.KEY_DEFAULT, taskDecorator);
        threadFactory = ExecutorUtils.getThreadFactory(env, ExecutorUtils.KEY_DEFAULT, threadFactory);
    }

}
