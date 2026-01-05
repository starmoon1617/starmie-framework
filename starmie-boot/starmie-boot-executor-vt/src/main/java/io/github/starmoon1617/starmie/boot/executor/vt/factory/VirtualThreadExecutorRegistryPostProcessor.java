/*
 * Copyright (c) 2025, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.boot.executor.vt.factory;

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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.StringUtils;

import io.github.starmoon1617.starmie.boot.executor.vt.util.VirtualThreadExecutorUtils;

/**
 * Help to registry SimpleAsyncTaskExecutor (Virtual Thread) as bean with configuration
 * starmie.executor.vt.names
 * 
 * @date 2025-12-20
 * @author Nathan Liao
 */
public class VirtualThreadExecutorRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualThreadExecutorRegistryPostProcessor.class);

    private ApplicationContext applicationContext;

    /**
     * default task decorator beanName
     */
    private String taskDecorator = "";
    
    /**
     * default thread name prefix
     */
    private String threadNamePrefix = "";

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
        Set<String> names = VirtualThreadExecutorUtils.getNames(env);
        if (names == null || names.isEmpty()) {
            LOGGER.debug("No virtual thread executor names found by configuration : {}{} ", VirtualThreadExecutorUtils.PROPERTY_PREFIX, VirtualThreadExecutorUtils.NAMES);
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
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(SimpleAsyncTaskExecutor.class);

        String beanName = VirtualThreadExecutorUtils.getExecutorName(name);
        builder.addPropertyValue("virtualThreads", true);
        
        String tNamePrefix = VirtualThreadExecutorUtils.getThreadNamePrefix(env, name, threadNamePrefix);
        if (!StringUtils.hasText(tNamePrefix)) {
            tNamePrefix = VirtualThreadExecutorUtils.getExecutorThreadName(name);
        }
        builder.addPropertyValue("threadNamePrefix", tNamePrefix);

        String decorator = VirtualThreadExecutorUtils.getTaskDecorator(env, name, taskDecorator);
        if (StringUtils.hasText(decorator)) {
            builder.addPropertyReference("taskDecorator", decorator);
        }

        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
        LOGGER.debug("Registered Virtual Thread Executor(name={})", beanName);
    }

    /**
     * over write default properties
     * 
     * @param env
     */
    protected void initDefaultProperties(Environment env) {
        taskDecorator = VirtualThreadExecutorUtils.getTaskDecorator(env, VirtualThreadExecutorUtils.KEY_DEFAULT, taskDecorator);
        threadNamePrefix = VirtualThreadExecutorUtils.getThreadNamePrefix(env, VirtualThreadExecutorUtils.KEY_DEFAULT, threadNamePrefix);
    }

}
