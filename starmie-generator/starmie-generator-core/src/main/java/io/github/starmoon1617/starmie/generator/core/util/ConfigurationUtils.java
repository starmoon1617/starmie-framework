/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.util;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.PropertyHolder;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.util.StringUtility;

import io.github.starmoon1617.starmie.generator.core.config.ConfigurationPropertyRegistry;
import io.github.starmoon1617.starmie.generator.core.config.CoreContext;
import io.github.starmoon1617.starmie.generator.core.config.CorePropertyRegistry;
import io.github.starmoon1617.starmie.generator.core.constant.Constants;
import io.github.starmoon1617.starmie.generator.core.mybatis3.StarmieIntrospectedTableImpl;
import io.github.starmoon1617.starmie.generator.core.types.StarmieJavaTypeResolverImpl;

/**
 * Help to construct Configurations
 * 
 * @date 2023-10-20
 * @author Nathan Liao
 */
public class ConfigurationUtils {

    private ConfigurationUtils() {

    }

    /**
     * create JDBC Connection Configuration
     * 
     * @param properties
     * @return
     */
    public static final JDBCConnectionConfiguration createJDBCConnectionConfiguration(Properties properties) {
        return createJDBCConnectionConfiguration(properties.getProperty(ConfigurationPropertyRegistry.CONNECTION_DRIVER_CLASS, ""),
                properties.getProperty(ConfigurationPropertyRegistry.CONNECTION_CONNECTION_URL, ""),
                properties.getProperty(ConfigurationPropertyRegistry.CONNECTION_USER_ID, ""),
                properties.getProperty(ConfigurationPropertyRegistry.CONNECTION_PASSWORD, ""));
    }

    /**
     * create JDBC Connection Configuration
     * 
     * @param driver
     * @param dbUri
     * @param user
     * @param password
     * @return
     */
    public static final JDBCConnectionConfiguration createJDBCConnectionConfiguration(String driver, String dbUri, String user, String password) {
        JDBCConnectionConfiguration connectionConfiguration = new JDBCConnectionConfiguration();
        connectionConfiguration.setDriverClass(driver);
        connectionConfiguration.setConnectionURL(dbUri);
        connectionConfiguration.setUserId(user);
        connectionConfiguration.setPassword(password);
        connectionConfiguration.addProperty("nullCatalogMeansCurrent", "true");
        return connectionConfiguration;
    }

    /**
     * create Java Type Resolver Configuration
     * 
     * @param properties
     * @return
     */
    public static final JavaTypeResolverConfiguration createJavaTypeResolverConfiguration(Properties properties) {
        return createJavaTypeResolverConfiguration(
                properties.getProperty(ConfigurationPropertyRegistry.TYPE_RESOLVER_TYPE, StarmieJavaTypeResolverImpl.class.getName()),
                properties.getProperty(
                        ConfigurationPropertyRegistry.TYPE_RESOLVER + Constants.DOT + CorePropertyRegistry.TYPE_RESOLVER_FORCE_TINYINT_TO_INTEGER, "true"),
                properties.getProperty(ConfigurationPropertyRegistry.TYPE_RESOLVER + Constants.DOT + PropertyRegistry.TYPE_RESOLVER_FORCE_BIG_DECIMALS, ""),
                properties.getProperty(ConfigurationPropertyRegistry.TYPE_RESOLVER + Constants.DOT + PropertyRegistry.TYPE_RESOLVER_USE_JSR310_TYPES, ""));
    }

    /**
     * create Java Type Resolver Configuration
     * 
     * @param type
     * @param forceTinyIntToInteger
     * @param forceBigDecimals
     * @param useJSR310Types
     * @return
     */
    public static final JavaTypeResolverConfiguration createJavaTypeResolverConfiguration(String type, String forceTinyIntToInteger, String forceBigDecimals,
            String useJSR310Types) {
        JavaTypeResolverConfiguration typeResolverConfiguration = new JavaTypeResolverConfiguration();
        typeResolverConfiguration.setConfigurationType(type);
        if (StringUtility.stringHasValue(forceTinyIntToInteger)) {
            typeResolverConfiguration.addProperty(CorePropertyRegistry.TYPE_RESOLVER_FORCE_TINYINT_TO_INTEGER, forceTinyIntToInteger);
        }
        if (StringUtility.stringHasValue(forceBigDecimals)) {
            typeResolverConfiguration.addProperty(PropertyRegistry.TYPE_RESOLVER_FORCE_BIG_DECIMALS, forceBigDecimals);
        }
        if (StringUtility.stringHasValue(useJSR310Types)) {
            typeResolverConfiguration.addProperty(PropertyRegistry.TYPE_RESOLVER_USE_JSR310_TYPES, useJSR310Types);
        }
        return typeResolverConfiguration;
    }

    /**
     * create Java Model Generator Configuration
     * 
     * @param properties
     * @return
     */
    public static final JavaModelGeneratorConfiguration createJavaModelGeneratorConfiguration(Properties properties) {
        JavaModelGeneratorConfiguration modelGeneratorConfiguration = new JavaModelGeneratorConfiguration();

        modelGeneratorConfiguration
                .setTargetPackage(properties.getProperty(CorePropertyRegistry.GENERATE_MODEL + Constants.DOT + CorePropertyRegistry.ANY_TARGET_PACKAGE, ""));
        modelGeneratorConfiguration
                .setTargetProject(properties.getProperty(CorePropertyRegistry.GENERATE_MODEL + Constants.DOT + CorePropertyRegistry.ANY_TARGET_PROJECT, ""));
        addProperties(properties, modelGeneratorConfiguration, CorePropertyRegistry.GENERATE_MODEL + Constants.DOT);
        return modelGeneratorConfiguration;
    }

    /**
     * create Sql Map Generator Configuration
     * 
     * @param properties
     * @return
     */
    public static final SqlMapGeneratorConfiguration createSqlMapGeneratorConfiguration(Properties properties) {
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();

        sqlMapGeneratorConfiguration
                .setTargetPackage(properties.getProperty(CorePropertyRegistry.GENERATE_SQL_MAP + Constants.DOT + CorePropertyRegistry.ANY_TARGET_PACKAGE, ""));
        sqlMapGeneratorConfiguration
                .setTargetProject(properties.getProperty(CorePropertyRegistry.GENERATE_SQL_MAP + Constants.DOT + CorePropertyRegistry.ANY_TARGET_PROJECT, ""));
        addProperties(properties, sqlMapGeneratorConfiguration, CorePropertyRegistry.GENERATE_SQL_MAP + Constants.DOT);
        return sqlMapGeneratorConfiguration;
    }

    /**
     * create Java Client Generator Configuration
     * 
     * @param properties
     * @param prefix
     * @return
     */
    public static final JavaClientGeneratorConfiguration createJavaClientGeneratorConfiguration(Properties properties, String prefix) {
        JavaClientGeneratorConfiguration clientGeneratorConfiguration = new JavaClientGeneratorConfiguration();

        clientGeneratorConfiguration.setTargetPackage(properties.getProperty(prefix + CorePropertyRegistry.ANY_TARGET_PACKAGE, ""));
        clientGeneratorConfiguration.setTargetProject(properties.getProperty(prefix + CorePropertyRegistry.ANY_TARGET_PROJECT, ""));
        addProperties(properties, clientGeneratorConfiguration, prefix);

        return clientGeneratorConfiguration;
    }

    /**
     * add prefix property to PropertyHolder
     * 
     * @param properties
     * @param propertyHolder
     * @param prefix
     */
    protected static void addProperties(Properties properties, PropertyHolder propertyHolder, String prefix) {
        Set<Entry<Object, Object>> propSet = properties.entrySet();
        for (Entry<Object, Object> prop : propSet) {
            String key = prop.getKey().toString();
            if (key.startsWith(prefix)) {
                propertyHolder.addProperty(key.substring(prefix.length()), prop.getValue().toString());
            }
        }
    }

    /**
     * add Table Configuration
     * 
     * @param tableName
     * @return
     */
    public static final void addTableConfiguration(Context context, String tableName, String catalog, String schema) {
        if (!StringUtility.stringHasValue(tableName)) {
            return;
        }
        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(tableName);
        tableConfiguration.setCatalog(catalog);
        tableConfiguration.setSchema(schema);
        tableConfiguration.setSelectByExampleStatementEnabled(false);
        tableConfiguration.setDeleteByExampleStatementEnabled(false);
        tableConfiguration.setCountByExampleStatementEnabled(false);
        tableConfiguration.setUpdateByExampleStatementEnabled(false);
        context.addTableConfiguration(tableConfiguration);
    }

    /**
     * create Configuration
     * 
     * @param properties
     * @return
     */
    public static final Configuration createConfiguration(Properties properties) {
        Configuration configuration = new Configuration();
        CoreContext context = new CoreContext(null);
        configuration.addContext(context);

        context.setId(properties.getProperty(ConfigurationPropertyRegistry.CONTEXT_ID, "Starmie-Generate"));
        context.setTargetRuntime(properties.getProperty(ConfigurationPropertyRegistry.CONTEXT_RUNTIME, StarmieIntrospectedTableImpl.class.getName()));
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING,
                properties.getProperty(ConfigurationPropertyRegistry.CONTEXT_FILE_ENCODING, Constants.UTF_8));
        context.addProperty(CorePropertyRegistry.TEMPLATE_GENERATOR_IMPL, properties.getProperty(ConfigurationPropertyRegistry.CONTEXT_TEMPLATE_GENERATOR, ""));

        context.setJdbcConnectionConfiguration(createJDBCConnectionConfiguration(properties));
        context.setJavaTypeResolverConfiguration(createJavaTypeResolverConfiguration(properties));

        context.setJavaModelGeneratorConfiguration(createJavaModelGeneratorConfiguration(properties));
        context.setSqlMapGeneratorConfiguration(createSqlMapGeneratorConfiguration(properties));
        context.setJavaClientGeneratorConfiguration(
                createJavaClientGeneratorConfiguration(properties, CorePropertyRegistry.GENERATE_MAP_CLIENT + Constants.DOT));
        context.getJavaClientGeneratorConfiguration().setConfigurationType(
                properties.getProperty(CorePropertyRegistry.GENERATE_MAP_CLIENT + Constants.DOT + ConfigurationPropertyRegistry.TYPE, "XMLMAPPER"));

        context.setServiceGeneratorConfiguration(createJavaClientGeneratorConfiguration(properties, CorePropertyRegistry.GENERATE_SERVICE + Constants.DOT));
        context.setManagerGeneratorConfiguration(createJavaClientGeneratorConfiguration(properties, CorePropertyRegistry.GENERATE_MANAGER + Constants.DOT));
        context.setControllerGeneratorConfiguration(
                createJavaClientGeneratorConfiguration(properties, CorePropertyRegistry.GENERATE_CONTROLLER + Constants.DOT));
        context.setScriptGeneratorConfiguration(createJavaClientGeneratorConfiguration(properties, CorePropertyRegistry.GENERATE_SCRIPT + Constants.DOT));
        context.setTextGeneratorConfiguration(createJavaClientGeneratorConfiguration(properties, CorePropertyRegistry.GENERATE_TEXT + Constants.DOT));

        String tableNames = properties.getProperty(ConfigurationPropertyRegistry.TABLE_NAME, Constants.PERCENT);
        String catalog = properties.getProperty(ConfigurationPropertyRegistry.CATALOG, null);
        String schema = properties.getProperty(ConfigurationPropertyRegistry.SCHEMA, null);
        Set<String> tables = StringUtility.tokenize(tableNames);
        for (String tableName : tables) {
            addTableConfiguration(context, tableName, catalog, schema);
        }
        return configuration;
    }
}
