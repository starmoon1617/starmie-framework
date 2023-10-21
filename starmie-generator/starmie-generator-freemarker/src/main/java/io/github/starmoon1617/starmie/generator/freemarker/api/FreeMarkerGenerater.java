/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.freemarker.api;

import java.io.StringWriter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.starmoon1617.starmie.generator.core.api.TemplateGenerator;
import io.github.starmoon1617.starmie.generator.freemarker.util.TemplateUtils;

/**
 * FreeMarker Template Generater
 * 
 * @date 2023-10-13
 * @author Nathan Liao
 */
public class FreeMarkerGenerater extends TemplateGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreeMarkerGenerater.class);

    @Override
    public String process(String template, Map<String, Object> datas) {
        StringWriter sw = new StringWriter();
        try {
            TemplateUtils.getTemplate(getTemplatePath(), template).process(datas, sw);
        } catch (Exception e) {
            LOGGER.error("FreeMarker process error : {}", e.getMessage());
        }
        return sw.toString();
    }

}
