/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.data;

import java.util.List;

/**
 * data for Java file
 * 
 * @date 2023-10-11
 * @author Nathan Liao
 */
public class JavaData extends BaseData {
    
    private static final long serialVersionUID = 1775908150992749635L;

    /**
     * comment for file, using to write copyright;
     */
    private String fileComent;
    
    /**
     * name for package
     */
    private String packageName;
    
    /**
     * import list
     */
    private List<String> imports;
    
    /**
     * super class name
     */
    private String superClass;
    
    /**
     * interface list
     */
    private List<String> interfaces;
    
    /**
     * fields define for the class
     */
    private List<FieldData> fields;
    
    /**
     * methods define for the class
     */
    private List<MethodData> methods;

    /**
     * @return the fileComent
     */
    public String getFileComent() {
        return fileComent;
    }

    /**
     * @param fileComent the fileComent to set
     */
    public void setFileComent(String fileComent) {
        this.fileComent = fileComent;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the imports
     */
    public List<String> getImports() {
        return imports;
    }

    /**
     * @param imports the imports to set
     */
    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    /**
     * @return the superClass
     */
    public String getSuperClass() {
        return superClass;
    }

    /**
     * @param superClass the superClass to set
     */
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    /**
     * @return the interfaces
     */
    public List<String> getInterfaces() {
        return interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * @return the fields
     */
    public List<FieldData> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(List<FieldData> fields) {
        this.fields = fields;
    }

    /**
     * @return the methods
     */
    public List<MethodData> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(List<MethodData> methods) {
        this.methods = methods;
    }
    
    
    
}
