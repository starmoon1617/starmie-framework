/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.data;

import java.util.List;

/**
 * data for a field
 * 
 * @date 2023-10-11
 * @author Nathan Liao
 */
public class FieldData extends BaseData {

    private static final long serialVersionUID = -4502279986000320324L;
    /**
     * modifiers for a field
     */
    private List<String> modifiers;

    /**
     * type
     */
    private String type;

    /**
     * value
     */
    private String value;

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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
