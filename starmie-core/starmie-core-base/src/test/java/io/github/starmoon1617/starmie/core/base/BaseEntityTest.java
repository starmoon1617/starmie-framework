/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.base;

import java.lang.reflect.TypeVariable;

/**
 * 
 * 
 * @date 2023-10-18
 * @author Nathan Liao
 */
public class BaseEntityTest {

    public static void main(String[] args) {
        Class<?> clazz = BaseEntity.class;

        TypeVariable<?>[] typeVariables = (TypeVariable<?>[]) clazz.getTypeParameters();
        if (typeVariables != null && typeVariables.length > 0) {
            for(TypeVariable<?> tv : typeVariables) {
                System.out.println("name == > " + tv.getName() + ", type name ==> " + tv.getTypeName());
            }
        }
    }

}
