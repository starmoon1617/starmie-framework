/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.starmoon1617.starmie.generator.core.constant.Constants;

/**
 * Help to add process types for Class
 * 
 * @date 2023-10-18
 * @author Nathan Liao
 */
public class TypeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeUtils.class);

    private TypeUtils() {

    }

    /**
     * get Abstract Method list from class
     * 
     * @param clazz
     * @return
     */
    public static List<Method> getAbstractMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        if (clazz == null) {
            return methods;
        }
        Method[] ms = clazz.getDeclaredMethods();
        if (ms != null && ms.length > 0) {
            for (Method m : ms) {
                if (m != null && Modifier.isAbstract(m.getModifiers()) && !Modifier.isStatic(m.getModifiers())) {
                    methods.add(m);
                }
            }
        }
        methods.addAll(getAbstractMethods(clazz.getSuperclass()));
        return methods;
    }

    /**
     * Load Class
     * 
     * @param clazz
     * @return
     */
    public static Class<?> loadClass(String clazz) {
        if (clazz == null) {
            return null;
        }
        Class<?> c = null;
        try {
            c = Class.forName(clazz);
        } catch (Exception e) {
            LOGGER.error(String.format("Load Class[{}] Error: ", clazz), e);
        }
        return c;
    }

    /**
     * 
     * @param parameterizedTypes
     * @param imports
     * @return
     */
    public static String getParameterizedTypes(String parameterizedTypes, Set<String> imports) {
        List<String> types = StringUtils.split(parameterizedTypes);
        if (types == null || types.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (String type : types) {
            Class<?> clazz = loadClass(type);
            if (clazz == null) {
                continue;
            }
            ImportUtils.addImportType(imports, clazz.getName());
            sb.append(clazz.getSimpleName());
            sb.append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            sb.insert(0, Constants.LESS_THAN);
            sb.append(Constants.GREATER_THAN);
        }
        return sb.toString();
    }

    /**
     * check if Implement Serializable interface
     * 
     * @param clazz
     * @return
     */
    public static boolean isSerializable(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null && interfaces.length > 0) {
            for (Class<?> i : interfaces) {
                if (Serializable.class.equals(i)) {
                    return true;
                }
            }
        }
        // check all super class
        return isSerializable(clazz.getSuperclass());
    }
}
