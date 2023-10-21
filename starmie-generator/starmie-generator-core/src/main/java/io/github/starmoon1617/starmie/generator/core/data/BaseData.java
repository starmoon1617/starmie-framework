/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.data;

import java.io.Serializable;
import java.util.List;

/**
 * base data for java file
 * 
 * @date 2023-10-11
 * @author Nathan Liao
 */
public class BaseData implements Serializable {
    
    private static final long serialVersionUID = 4769324161558650001L;

    /**
     * name for class, field, method
     */
    private String name;
    
    /**
     * comment for class, field, method
     */
    private String comment;
    
    /**
     * annotations for class, field, method
     */
    private List<String> annotations;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the annotations
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /**
     * @param annotations the annotations to set
     */
    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }
}
