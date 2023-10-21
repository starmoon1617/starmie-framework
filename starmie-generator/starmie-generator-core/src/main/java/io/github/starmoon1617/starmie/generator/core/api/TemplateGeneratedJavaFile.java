/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.api;

import java.util.Map;

import org.mybatis.generator.api.GeneratedJavaFile;

/**
 * Generate Java file using template
 * 
 * @date 2023-10-12
 * @author Nathan Liao
 */
public class TemplateGeneratedJavaFile extends GeneratedJavaFile {

    /**
     * data
     */
    private Map<String, Object> datas;

    /**
     * target package name
     */
    private String targetPackage;

    /**
     * java file name
     */
    private String fileName;

    /**
     * template to generate
     */
    private TemplateGenerator template;

    /**
     * template file to use
     */
    private String templateFile;

    /**
     * @param compilationUnit
     * @param targetProject
     * @param fileEncoding
     * @param javaFormatter
     */
    public TemplateGeneratedJavaFile(TemplateGenerator template, String templateFile, Map<String, Object> datas, String fileName, String targetPackage,
            String targetProject, String fileEncoding) {
        super(null, targetProject, fileEncoding, null);
        this.datas = datas;
        this.template = template;
        this.targetPackage = targetPackage;
        this.fileName = fileName;
        this.templateFile = templateFile;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getTargetPackage() {
        return targetPackage;
    }

    @Override
    public String getFormattedContent() {
        return template.process(templateFile, datas);
    }

}
