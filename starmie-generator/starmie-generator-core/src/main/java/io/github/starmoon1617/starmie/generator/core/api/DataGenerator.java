/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.api;

import java.util.Set;

/**
 * Base interface for field/method/generic generator
 * 
 * @date 2023-10-27
 * @author Nathan Liao
 */
public interface DataGenerator {
    
    /**
     * return import list
     * 
     * @return
     */
    Set<String> getImports();
    
}
