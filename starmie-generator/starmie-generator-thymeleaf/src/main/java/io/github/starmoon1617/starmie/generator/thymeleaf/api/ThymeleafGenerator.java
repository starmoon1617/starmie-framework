/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.thymeleaf.api;

import java.io.StringWriter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import io.github.starmoon1617.starmie.generator.core.api.TemplateGenerator;
import io.github.starmoon1617.starmie.generator.thymeleaf.util.TemplateEngineUtils;

/**
 * Thymeleaf Template Generator
 * 
 * @date 2023-10-13
 * @author Nathan Liao
 */
public class ThymeleafGenerator extends TemplateGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafGenerator.class);

    @Override
    public String process(String template, Map<String, Object> datas) {
        StringWriter sw = new StringWriter();
        try {
            Context context = new Context();
            context.setVariables(datas);
            TemplateEngineUtils.getTemplateEngine(getTemplatePath(), getTemplateExt()).process(template, context, sw);
        } catch (Exception e) {
            LOGGER.error("Thymeleaf process error : {}", e.getMessage());
        }
        return sw.toString();
    }

}
