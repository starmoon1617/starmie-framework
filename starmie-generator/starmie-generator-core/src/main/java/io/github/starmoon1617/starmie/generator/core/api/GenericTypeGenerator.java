/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.api;

import java.util.Map;

/**
 * Interface to generate Generic Type for class
 * 
 * @date 2023-10-27
 * @author Nathan Liao
 */
public interface GenericTypeGenerator extends DataGenerator {
    
    /**
     * create Generic Type String (not include "&lt;" and ">")
     * @return
     */
    String generate(Map<String, Object> attrs);
}
