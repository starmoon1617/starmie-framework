/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.freemarker.util;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 模版工具类, 根据传入的模板路径获取template对象
 * Utility Class for FreeMarker Template
 * 
 * @date 2023-10-13
 * @author Nathan Liao
 */
public class TemplateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateUtils.class);

    private static Configuration CONFIGURATION;

    private TemplateUtils() {

    }

    static {
        try {
            CONFIGURATION = new Configuration(Configuration.VERSION_2_3_32);
            CONFIGURATION.setDefaultEncoding(StandardCharsets.UTF_8.name());
            CONFIGURATION.setLogTemplateExceptions(false);
        } catch (Exception e) {
            LOGGER.error("Create template configuration error : {} ", e.getMessage());
        }
    }

    /**
     * get Template 
     * 
     * @param path - 模版保存的路径
     * @param name - 模版的文件名
     * @return
     */
    public static Template getTemplate(String path, String name) {
        Template template = null;
        if (CONFIGURATION != null) {
            try {
                CONFIGURATION.setClassLoaderForTemplateLoading(Thread.currentThread().getContextClassLoader(), path);
                template = CONFIGURATION.getTemplate(name);
            } catch (Exception e) {
                LOGGER.error("Create template error : {} ", e.getMessage());
                template = null;
            }
        }
        return template;
    }
}
