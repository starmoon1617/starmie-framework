/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.config;

import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.ModelType;

/**
 * 继承mybatis Generator的context对象,添加Service, Manager,Controller, JavaScript,
 * View等配置 <br>
 * extends from context, add configuration for Service, Manager,Controller,
 * JavaScript, View etc
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public class CoreContext extends Context {

    /**
     * configuration for Service
     */
    private JavaClientGeneratorConfiguration serviceGeneratorConfiguration;
    /**
     * configuration for Manager
     */
    private JavaClientGeneratorConfiguration managerGeneratorConfiguration;
    /**
     * configuration for Controller
     */
    private JavaClientGeneratorConfiguration controllerGeneratorConfiguration;
    /**
     * configuration for Script (JavaScript etc)
     */
    private JavaClientGeneratorConfiguration scriptGeneratorConfiguration;
    /**
     * configuration for text (HTML etc)
     */
    private JavaClientGeneratorConfiguration textGeneratorConfiguration;

    /**
     * @param defaultModelType
     */
    public CoreContext(ModelType defaultModelType) {
        super(defaultModelType);
    }

    /**
     * @return the serviceGeneratorConfiguration
     */
    public JavaClientGeneratorConfiguration getServiceGeneratorConfiguration() {
        return serviceGeneratorConfiguration;
    }

    /**
     * @param serviceGeneratorConfiguration
     *            the serviceGeneratorConfiguration to set
     */
    public void setServiceGeneratorConfiguration(JavaClientGeneratorConfiguration serviceGeneratorConfiguration) {
        this.serviceGeneratorConfiguration = serviceGeneratorConfiguration;
    }

    /**
     * @return the managerGeneratorConfiguration
     */
    public JavaClientGeneratorConfiguration getManagerGeneratorConfiguration() {
        return managerGeneratorConfiguration;
    }

    /**
     * @param managerGeneratorConfiguration
     *            the managerGeneratorConfiguration to set
     */
    public void setManagerGeneratorConfiguration(JavaClientGeneratorConfiguration managerGeneratorConfiguration) {
        this.managerGeneratorConfiguration = managerGeneratorConfiguration;
    }

    /**
     * @return the controllerGeneratorConfiguration
     */
    public JavaClientGeneratorConfiguration getControllerGeneratorConfiguration() {
        return controllerGeneratorConfiguration;
    }

    /**
     * @param controllerGeneratorConfiguration
     *            the controllerGeneratorConfiguration to set
     */
    public void setControllerGeneratorConfiguration(JavaClientGeneratorConfiguration controllerGeneratorConfiguration) {
        this.controllerGeneratorConfiguration = controllerGeneratorConfiguration;
    }

    /**
     * @return the scriptGeneratorConfiguration
     */
    public JavaClientGeneratorConfiguration getScriptGeneratorConfiguration() {
        return scriptGeneratorConfiguration;
    }

    /**
     * @param scriptGeneratorConfiguration
     *            the scriptGeneratorConfiguration to set
     */
    public void setScriptGeneratorConfiguration(JavaClientGeneratorConfiguration scriptGeneratorConfiguration) {
        this.scriptGeneratorConfiguration = scriptGeneratorConfiguration;
    }

    /**
     * @return the textGeneratorConfiguration
     */
    public JavaClientGeneratorConfiguration getTextGeneratorConfiguration() {
        return textGeneratorConfiguration;
    }

    /**
     * @param textGeneratorConfiguration
     *            the textGeneratorConfiguration to set
     */
    public void setTextGeneratorConfiguration(JavaClientGeneratorConfiguration textGeneratorConfiguration) {
        this.textGeneratorConfiguration = textGeneratorConfiguration;
    }

}
