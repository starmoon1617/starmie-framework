/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.mybatis.generator.internal.util.StringUtility;

import io.github.starmoon1617.starmie.generator.core.constant.Constants;

/**
 * Utility Class for String
 * 
 * @date 2023-10-20
 * @author Nathan Liao
 */
public class StringUtils {

    private StringUtils() {

    }

    /**
     * split to list
     * 
     * @param str
     * @return
     */
    public static List<String> split(String str) {
        if (!StringUtility.stringHasValue(str)) {
            return null;
        }
        List<String> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(str, Constants.COMMA);
        while (st.hasMoreTokens()) {
            String s = st.nextToken().trim();
            if (s.length() > 0) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * change first char to upper case
     * 
     * @param str
     * @return
     */
    public static final String capitalize(String str) {
        if (!StringUtility.stringHasValue(str)) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return (new StringBuilder(Character.toUpperCase(str.charAt(0)))).append(str.substring(1)).toString();
    }

    /**
     * change first char to lower case
     * 
     * @param str
     * @return
     */
    public static final String uncapitalize(String str) {
        if (!StringUtility.stringHasValue(str)) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return (new StringBuilder("")).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
    }

}
