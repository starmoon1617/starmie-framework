/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.api;

import java.util.Map;

import io.github.starmoon1617.starmie.generator.core.constant.Constants;

/**
 * base class for template
 * 
 * @date 2023-10-12
 * @author Nathan Liao
 */
public abstract class TemplateGenerator {

    /**
     * path for template
     */
    private String templatePath;

    /**
     * ext for template
     */
    private String templateExt;

    /**
     * process template to file content
     * 
     * @param template
     * @param datas
     * @return
     */
    public abstract String process(String template, Map<String, Object> datas);

    /**
     * @return the templatePath
     */
    public String getTemplatePath() {
        return templatePath;
    }

    /**
     * @param templatePath
     *            the templatePath to set
     */
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * @return the templateExt
     */
    public String getTemplateExt() {
        return (templateExt.startsWith(Constants.DOT) ? templateExt : Constants.DOT.concat(templateExt));
    }

    /**
     * @param templateExt
     *            the templateExt to set
     */
    public void setTemplateExt(String templateExt) {
        this.templateExt = templateExt;
    }

}
