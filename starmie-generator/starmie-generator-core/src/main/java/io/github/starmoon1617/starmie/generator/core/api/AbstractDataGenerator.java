/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.api;

import java.util.Set;
import java.util.TreeSet;

import io.github.starmoon1617.starmie.generator.core.util.ImportUtils;

/**
 * Abstract java data Generator
 * 
 * @date 2023-10-27
 * @author Nathan Liao
 */
public abstract class AbstractDataGenerator implements DataGenerator {

    private Set<String> imports = new TreeSet<>();

    /**
     * @return the imports
     */
    public Set<String> getImports() {
        return imports;
    }

    /**
     * add new import class
     * 
     * @param type
     */
    public void addImports(String type) {
        ImportUtils.addImportType(imports, type);
    }
}
