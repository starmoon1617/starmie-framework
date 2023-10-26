/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.config;

/**
 * Property for JDBC connection
 * 
 * @date 2023-10-20
 * @author Nathan Liao
 */
public class ConfigurationPropertyRegistry {

    private ConfigurationPropertyRegistry() {

    }
    
    /**
     * table name
     */
    public static final String TABLE_NAME = "tableName";

    /**
     * context id
     */
    public static final String CONTEXT_ID = "context.id";

    /**
     * runtime
     */
    public static final String CONTEXT_RUNTIME = "context.runtime";
    
    /**
     * fileEncoding
     */
    public static final String CONTEXT_FILE_ENCODING = "context.fileEncoding";
    
    /**
     * fileEncoding
     */
    public static final String CONTEXT_TEMPLATE_GENERATOR = "context.templateGenerator";

    /**
     * driverClass
     */
    public static final String CONNECTION_DRIVER_CLASS = "jdbcConnection.driverClass";

    /**
     * connectionURL
     */
    public static final String CONNECTION_CONNECTION_URL = "jdbcConnection.connectionURL";

    /**
     * userId
     */
    public static final String CONNECTION_USER_ID = "jdbcConnection.userId";

    /**
     * password
     */
    public static final String CONNECTION_PASSWORD = "jdbcConnection.password";

    /**
     * javaTypeResolver.type
     */
    public static final String TYPE_RESOLVER_TYPE = "javaTypeResolver.type";

    /**
     * javaTypeResolver
     */
    public static final String TYPE_RESOLVER = "javaTypeResolver";
    
    /**
     * type 
     */
    public static final String TYPE = "type";

}
