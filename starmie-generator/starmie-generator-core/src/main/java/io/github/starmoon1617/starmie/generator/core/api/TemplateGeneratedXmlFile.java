/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.api;

import java.util.Map;

import org.mybatis.generator.api.GeneratedXmlFile;

/**
 * Generate xml file using template
 * 
 * @date 2023-10-12
 * @author Nathan Liao
 */
public class TemplateGeneratedXmlFile extends GeneratedXmlFile {

    /**
     * data
     */
    private Map<String, Object> datas;

    /**
     * template to generate
     */
    private TemplateGenerator template;

    /**
     * template file to use
     */
    private String templateFile;

    /**
     * @param template
     * @param templateFile
     * @param datas
     * @param fileName
     * @param targetPackage
     * @param targetProject
     */
    public TemplateGeneratedXmlFile(TemplateGenerator template, String templateFile, Map<String, Object> datas, String fileName, String targetPackage,
            String targetProject) {
        super(null, fileName, targetPackage, targetProject, false, null);
        this.datas = datas;
        this.template = template;
        this.templateFile = templateFile;
    }
    
    @Override
    public String getFormattedContent() {
        return template.process(templateFile, datas);
    }

}
