/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.boot.executor.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.starmoon1617.starmie.boot.executor.factory.ExecutorRegistryPostProcessor;

/**
 * Executor Auto Configuration
 * 
 * @date 2023-10-25
 * @author Nathan Liao
 */
@Configuration
public class ExecutorAutoConfiguration {

    @Bean
    public static ExecutorRegistryPostProcessor executorRegistryPostProcessor() {
        return new ExecutorRegistryPostProcessor();
    }

}
