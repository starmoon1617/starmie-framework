/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.util;

import java.util.Set;

import org.mybatis.generator.internal.util.StringUtility;

/**
 * Help to add import to a Class file
 * 
 * @date 2023-10-16
 * @author Nathan Liao
 */
public class ImportUtils {

    private ImportUtils() {

    }

    /**
     * add a type to importTypes, exclude the type start with java.lang
     * 
     * @param importTypes
     * @param type
     */
    public static void addImportType(Set<String> importTypes, String type) {
        if (importTypes == null) {
            return;
        }
        if (!StringUtility.stringHasValue(type)) {
            return;
        }
        if (type.startsWith("java.lang")) {
            return;
        }
        importTypes.add(type);
    }
}
