/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.util;

import java.util.Collection;
import java.util.Map;

import org.springframework.util.StringUtils;

import io.github.starmoon1617.starmie.core.constant.InterpunctionConstants;

/**
 * String, Collection, Map等工具类 Utility Class for String, Collection, Map etc...
 * 
 * @date 2023-10-08
 * @author Nathan Liao
 */
public class CommonUtils {

    private CommonUtils() {

    }

    /**
     * Check whether the given String contains actual text
     * 
     * @param str
     * @return
     */
    public static final boolean isNotBlank(String str) {
        return StringUtils.hasText(str);
    }

    /**
     * 是否为非空集合, null, 或者没有元素时返回false
     * 
     * @param coll
     * @return
     */
    public static final boolean isEmpty(Collection<?> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * 是否为空Map, null, 或者没有元素的map返回true
     * 
     * @param map
     * @return
     */
    public static final boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 判断数组是否为空, 为null或者length为0返回true
     * 
     * @param array
     * @return
     */
    public static final boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * change first char to upper case
     * 
     * @param str
     * @return
     */
    public static final String capitalize(String str) {
        return StringUtils.capitalize(str);
    }

    /**
     * change first char to lower case
     * 
     * @param str
     * @return
     */
    public static final String uncapitalize(String str) {
        return StringUtils.uncapitalize(str);
    }

    /**
     * 分隔
     * 
     * @param str
     * @param delimiter
     * @return
     */
    public static final String[] split(String str, String delimiter) {
        return StringUtils.delimitedListToStringArray(str, delimiter);
    }

    /**
     * Camel to underscore
     * 
     * @param str
     * @return
     */
    public static final String toUnderScore(String str) {
        if (str == null) {
            return str;
        }
        int length = str.length();
        StringBuilder result = new StringBuilder(length * 2);
        int resultLength = 0;
        boolean wasPrevTranslated = false;
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (i > 0 || c != InterpunctionConstants.UNDER_LINE) {
                if (Character.isUpperCase(c)) {
                    if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != InterpunctionConstants.UNDER_LINE) {
                        result.append(InterpunctionConstants.UNDER_LINE);
                        resultLength++;
                    }
                    c = Character.toLowerCase(c);
                    wasPrevTranslated = true;
                } else {
                    wasPrevTranslated = false;
                }
                result.append(c);
                resultLength++;
            }
        }
        return (resultLength > 0 ? result.toString() : str);
    }

}
