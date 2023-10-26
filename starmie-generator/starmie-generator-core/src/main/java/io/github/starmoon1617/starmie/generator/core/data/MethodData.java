/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.data;

import java.util.List;

/**
 * data for a method
 * 
 * @date 2023-10-11
 * @author Nathan Liao
 */
public class MethodData extends BaseData {

    private static final long serialVersionUID = -7680226976578211635L;

    /**
     * modifiers for a method
     */
    private List<String> modifiers;

    /**
     * method parameters
     */
    private List<String> parameters;

    /**
     * implementation code for a method
     */
    private List<String> implementations;

    /**
     * return type
     */
    private String returnType;

    /**
     * @return the modifiers
     */
    public List<String> getModifiers() {
        return modifiers;
    }

    /**
     * @param modifiers
     *            the modifiers to set
     */
    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }

    /**
     * @return the parameters
     */
    public List<String> getParameters() {
        return parameters;
    }

    /**
     * @param parameters
     *            the parameters to set
     */
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the implementations
     */
    public List<String> getImplementations() {
        return implementations;
    }

    /**
     * @param implementations
     *            the implementations to set
     */
    public void setImplementations(List<String> implementations) {
        this.implementations = implementations;
    }

    /**
     * @return the returnType
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * @param returnType
     *            the returnType to set
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

}
