/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.config;

/**
 * Property to use
 * 
 * @date 2023-10-11
 * @author Nathan Liao
 */
public class CorePropertyRegistry {

    private CorePropertyRegistry() {

    }

    /**
     * generic string for root class
     */
    public static final String ANY_GENERIC_ROOT_CLASS = "genericRootClass";

    /**
     * class name for java model/mapper/manager/service/controller
     */
    public static final String ANY_CLASS = "class";

    /**
     * Package for java model/mapper/manager/service/controller
     */

    public static final String ANY_TARGET_PACKAGE = "targetPackage";
    /**
     * local for all files
     */
    public static final String ANY_TARGET_PROJECT = "targetProject";

    /**
     * folder for template
     */
    public static final String ANY_TEMPLATE_PATH = "templatePath";

    /**
     * file EXT for template
     */
    public static final String ANY_TEMPLATE_EXT = "templateExt";

    /**
     * file names for template
     */
    public static final String ANY_TEMPLATE_FILES = "templateFiles";

    /**
     * file EXT for files generated
     */
    public static final String ANY_FILE_EXT = "fileExt";
    
    /**
     * file name to use model name, only apply to first template
     */
    public static final String OVER_TEMPLATE_FILE_NAME = "overFileName";

    /**
     * generic type Generator
     */
    public static final String ANY_GENERIC_GENERATOR = "genericTypeGenerator";

    /**
     * field Generator
     */
    public static final String ANY_FIELD_GENERATOR = "fieldGenerator";

    /**
     * method Generator
     */
    public static final String ANY_METHOD_GENERATOR = "methodGenerator";

    /**
     * if use rest controller instead
     */
    public static final String REST_OVER_CONTROLLER = "restOverController";

    /**
     * model's columns
     */
    public static final String MODEL_COLUMNS = "modelColumns";

    /**
     * fields define in model's super class
     */
    public static final String SUPER_FIELDS = "superFields";

    /**
     * generic define in model's super class
     */
    public static final String SUPER_GENERIC = "superGeneric";

    /**
     * Generate Model
     */
    public static final String GENERATE_MODEL = "generateModel";

    /**
     * Generate Sql Map
     */
    public static final String GENERATE_SQL_MAP = "generateSqlMap";

    /**
     * Generate Map Client
     */
    public static final String GENERATE_MAP_CLIENT = "generateMapClient";

    /**
     * if generate Service
     */
    public static final String GENERATE_SERVICE = "generateService";

    /**
     * if generate Manager
     */
    public static final String GENERATE_MANAGER = "generateManager";

    /**
     * if generate Controller
     */
    public static final String GENERATE_CONTROLLER = "generateController";

    /**
     * if generate Rest Controller
     */
    public static final String ENABLE_REST_CONTROLLER = "enableRest";

    /**
     * if generate Script
     */
    public static final String GENERATE_SCRIPT = "generateScript";

    /**
     * if generate Text(HTML)
     */
    public static final String GENERATE_TEXT = "generateText";

    /**
     * Impl class for generator
     */
    public static final String TEMPLATE_GENERATOR_IMPL = "templateGeneratorImpl";

    /**
     * force TinyInt to Integer
     */
    public static final String TYPE_RESOLVER_FORCE_TINYINT_TO_INTEGER = "forceTinyIntToInteger";

    /**
     * Impl
     */
    public static final String IMPL = "Impl";
    /**
     * Model
     */
    public static final String MODEL = "Model";
    /**
     * Mapper
     */
    public static final String XML_MAPPER = "XmlMapper";
    /**
     * Mapper
     */
    public static final String MAPPER = "Mapper";
    /**
     * Service
     */
    public static final String SERVICE = "Service";

    /**
     * Manager
     */
    public static final String MANAGER = "Manager";

    /**
     * Controller
     */
    public static final String CONTROLLER = "Controller";

    /**
     * Script
     */
    public static final String SCRIPT = "Script";

    /**
     * Text(HTML)
     */
    public static final String TEXT = "Text";
}
