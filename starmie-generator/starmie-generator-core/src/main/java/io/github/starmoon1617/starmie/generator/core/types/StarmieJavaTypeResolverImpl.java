/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.types;

import java.sql.Types;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;
import org.mybatis.generator.internal.util.StringUtility;

import io.github.starmoon1617.starmie.generator.core.config.CorePropertyRegistry;

/**
 * 继承默认的实现,重写tinyInt生成为Integer类型
 * Extend JavaTypeResolverDefaultImpl, add tinyInt to integer resolver
 * 
 * @date 2023-10-11
 * @author Nathan Liao
 */
public class StarmieJavaTypeResolverImpl extends JavaTypeResolverDefaultImpl {
    
    /**
     * force TinyInt to Integer
     */
    protected boolean forceTinyIntToInteger;
    
    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        forceTinyIntToInteger = StringUtility.isTrue(properties.getProperty(CorePropertyRegistry.TYPE_RESOLVER_FORCE_TINYINT_TO_INTEGER));
    }
    
    @Override
    protected FullyQualifiedJavaType overrideDefaultType(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        if (forceTinyIntToInteger && Types.TINYINT == column.getJdbcType()) {
            return new FullyQualifiedJavaType(Integer.class.getName());
        } else {
            return super.overrideDefaultType(column, defaultType);
        }
    }
    
}
