/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.thymeleaf.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Utility Class for Thymeleaf Template
 * 
 * @date 2023-10-16
 * @author Nathan Liao
 */
public class TemplateEngineUtils {
    
    private TemplateEngineUtils() {
        
    }
    
    /**
     * get Template Engine
     * @param path
     * @param ext
     * @return
     */
    public static TemplateEngine getTemplateEngine(String path, String ext) {
        TemplateEngine templateEngine = new TemplateEngine();
        AbstractConfigurableTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setPrefix(path);
        templateResolver.setSuffix(ext);
        templateEngine.addTemplateResolver(templateResolver);
        return templateEngine;
    }
}
