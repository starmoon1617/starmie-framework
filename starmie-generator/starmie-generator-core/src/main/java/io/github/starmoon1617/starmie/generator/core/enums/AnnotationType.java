/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.enums;

/**
 * define Annotation in class file for code generator
 * 
 * @date 2023-10-11
 * @author Nathan Liao
 */
public enum AnnotationType {
    
    /**
     * Override Annotation
     */
    OVERRIDE("@Override", "java.lang.Override", "Override Annotation"),
    
    /**
     * Generated Annotation
     */
    GENERATED("@Generated", "jakarta.annotation.Generated", "Generated Annotation"),
    
    /**
     * Resource Annotation - jakarta.annotation.Resource
     */
    RESOURCE("@Resource", "jakarta.annotation.Resource", "Resource Annotation"),
    
    /**
     * Service Annotation
     */
    SERVICE("@Service", "org.springframework.stereotype.Service", "Service Annotation"),
    
    /**
     * Component Annotation
     */
    COMPONENT("@Component", "org.springframework.stereotype.Component", "Component Annotation"),
    
    /**
     * Controller Annotation
     */
    CONTROLLER("@Controller", "org.springframework.stereotype.Controller", "Controller Annotation"),
    
    /**
     * RestController Annotation
     */
    RESTCONTROLLER("@RestController", "org.springframework.web.bind.annotation.RestController", "RestController Annotation"),
    
    /**
     * Request mapping Annotation
     */
    REQUESTMAPPING("@Requestmapping", "org.springframework.web.bind.annotation.RequestMapping", "Requestmapping Annotation");

    /**
     * annotation string
     */
    private String annotation;

    /**
     * class name
     */
    private String className;

    /**
     * desc
     */
    private String desc;

    /**
     * @param annotation
     * @param className
     * @param desc
     */
    private AnnotationType(String annotation, String className, String desc) {
        this.annotation = annotation;
        this.className = className;
        this.desc = desc;
    }

    /**
     * @return the annotation
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

}
