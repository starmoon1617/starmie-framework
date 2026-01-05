/*
 * Copyright (c) 2025, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.boot.executor.vt.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.starmoon1617.starmie.boot.executor.vt.factory.VirtualThreadExecutorRegistryPostProcessor;

/**
 * Virtual Thread Executor Auto Configuration
 * 
 * @date 2025-12-20
 * @author Nathan Liao
 */
@Configuration
public class VirtualThreadExecutorAutoConfiguration {

    @Bean
    public static VirtualThreadExecutorRegistryPostProcessor virtualThreadExecutorRegistryPostProcessor() {
        return new VirtualThreadExecutorRegistryPostProcessor();
    }

}
