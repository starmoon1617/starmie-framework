/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.doc.convert;

/**
 * an interface to convert an Object to String
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public interface Converter<T> {

    /**
     * convert Object to String
     * 
     * @param t
     * @return
     */
    String convert(T t);
}
