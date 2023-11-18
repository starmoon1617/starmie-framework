/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.mybatis3;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.codegen.mybatis3.model.ExampleGenerator;
import org.mybatis.generator.codegen.mybatis3.model.PrimaryKeyGenerator;
import org.mybatis.generator.codegen.mybatis3.model.RecordWithBLOBsGenerator;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.PropertyHolder;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TypedPropertyHolder;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.StringUtility;

import io.github.starmoon1617.starmie.generator.core.api.FieldGenerator;
import io.github.starmoon1617.starmie.generator.core.api.GenericTypeGenerator;
import io.github.starmoon1617.starmie.generator.core.api.MethodGenerator;
import io.github.starmoon1617.starmie.generator.core.api.ModelFieldGenerator;
import io.github.starmoon1617.starmie.generator.core.api.ModelMethodGenerator;
import io.github.starmoon1617.starmie.generator.core.api.TemplateGeneratedJavaFile;
import io.github.starmoon1617.starmie.generator.core.api.TemplateGeneratedXmlFile;
import io.github.starmoon1617.starmie.generator.core.api.TemplateGenerator;
import io.github.starmoon1617.starmie.generator.core.config.CoreContext;
import io.github.starmoon1617.starmie.generator.core.config.CorePropertyRegistry;
import io.github.starmoon1617.starmie.generator.core.constant.CodeConstants;
import io.github.starmoon1617.starmie.generator.core.constant.Constants;
import io.github.starmoon1617.starmie.generator.core.data.ColumnData;
import io.github.starmoon1617.starmie.generator.core.data.FieldData;
import io.github.starmoon1617.starmie.generator.core.data.GenericTypeData;
import io.github.starmoon1617.starmie.generator.core.data.MethodData;
import io.github.starmoon1617.starmie.generator.core.enums.AnnotationType;
import io.github.starmoon1617.starmie.generator.core.extend.Counter;
import io.github.starmoon1617.starmie.generator.core.util.CommentsUtils;
import io.github.starmoon1617.starmie.generator.core.util.DateUtils;
import io.github.starmoon1617.starmie.generator.core.util.ImportUtils;
import io.github.starmoon1617.starmie.generator.core.util.StringUtils;
import io.github.starmoon1617.starmie.generator.core.util.TypeUtils;

/**
 * Extends IntrospectedTableMyBatis3Impl 继承IntrospectedTableMyBatis3Impl进行扩展
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public class StarmieIntrospectedTableImpl extends IntrospectedTableMyBatis3Impl {

    /**
     * class for
     */
    private Class<? extends TemplateGenerator> templateGeneratorClass;

    /**
     * Service/Manager/Controller Generator
     */
    protected List<AbstractJavaGenerator> javaGenerators;

    /**
     * Script Generator
     */
    protected List<AbstractJavaGenerator> scriptGenerators;

    /**
     * Text Generator
     */
    protected List<AbstractJavaGenerator> textGenerators;

    /**
     * Constructs a new StarmieIntrospectedTableImpl
     */
    public StarmieIntrospectedTableImpl() {
        super();
        javaGenerators = new ArrayList<>();
        scriptGenerators = new ArrayList<>();
        textGenerators = new ArrayList<>();
    }

    /**
     * 计算子包,由enableSubPackages配置,enableSubPackages为true则加上modelName到子包中
     * 
     * @param propertyHolder
     * @return
     */
    protected String calculateSubPackage(PropertyHolder propertyHolder) {
        StringBuilder sb = new StringBuilder();
        if (StringUtility.isTrue(propertyHolder.getProperty(PropertyRegistry.ANY_ENABLE_SUB_PACKAGES))) {
            sb.append('.');
            sb.append(fullyQualifiedTable.getDomainObjectName().toLowerCase());
        }
        return sb.toString();
    }

    /**
     * 从propertyHolder中讲配置设置到Attributes
     * 
     * @param propertyHolder
     * @param key
     * @param attributeKey
     */
    protected void setAttribute(PropertyHolder propertyHolder, String key, String attributeKey) {
        String value = propertyHolder.getProperty(key);
        if (StringUtility.stringHasValue(value)) {
            setAttribute(attributeKey, value);
        }
    }

    @Override
    protected void calculateXmlAttributes() {
        super.calculateXmlAttributes();
        SqlMapGeneratorConfiguration config = context.getSqlMapGeneratorConfiguration();
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_PATH, CorePropertyRegistry.XML_MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_FILES,
                CorePropertyRegistry.XML_MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_EXT, CorePropertyRegistry.XML_MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
    }

    @Override
    protected String calculateSqlMapPackage() {
        StringBuilder sb = new StringBuilder();
        SqlMapGeneratorConfiguration config = context.getSqlMapGeneratorConfiguration();
        // config can be null if the Java client does not require XML
        if (config != null) {
            sb.append(config.getTargetPackage());
            sb.append(calculateSubPackage(config));
            if (StringUtility.stringHasValue(tableConfiguration.getMapperName())) {
                String mapperName = tableConfiguration.getMapperName();
                int ind = mapperName.lastIndexOf('.');
                if (ind != -1) {
                    sb.append('.').append(mapperName.substring(0, ind));
                }
            }
        }
        return sb.toString();
    }

    @Override
    protected void calculateModelAttributes() {
        super.calculateModelAttributes();

        JavaModelGeneratorConfiguration config = context.getJavaModelGeneratorConfiguration();
        setAttribute(config, PropertyRegistry.ANY_ROOT_CLASS, CorePropertyRegistry.MODEL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);

        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_PATH, CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_FILES, CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_EXT, CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);

        // generator
        setAttribute(config, CorePropertyRegistry.ANY_GENERIC_GENERATOR,
                CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_GENERATOR);
        setAttribute(config, CorePropertyRegistry.ANY_FIELD_GENERATOR, CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_FIELD_GENERATOR);
        setAttribute(config, CorePropertyRegistry.ANY_METHOD_GENERATOR, CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_METHOD_GENERATOR);
    }

    @Override
    protected String calculateJavaModelPackage() {
        JavaModelGeneratorConfiguration config = context.getJavaModelGeneratorConfiguration();
        StringBuilder sb = new StringBuilder();
        sb.append(config.getTargetPackage());
        sb.append(calculateSubPackage(config));
        return sb.toString();
    }

    @Override
    protected void calculateJavaClientAttributes() {
        JavaClientGeneratorConfiguration config = context.getJavaClientGeneratorConfiguration();
        if (config == null) {
            return;
        }

        String interfacePackage = config.getTargetPackage();
        String classPackage = calculateJavaClientInterfacePackage();
        if (!StringUtility.stringHasValue(classPackage)) {
            classPackage = interfacePackage;
        }
        String subPackages = calculateSubPackage(config);
        String modelType = fullyQualifiedTable.getDomainObjectName();

        StringBuilder sb = new StringBuilder();
        sb.append(classPackage);
        sb.append(subPackages);
        sb.append('.');
        if (StringUtility.stringHasValue(tableConfiguration.getMapperName())) {
            sb.append(tableConfiguration.getMapperName());
        } else {
            sb.append(modelType);
            sb.append(CorePropertyRegistry.MAPPER); // $NON-NLS-1$
        }
        setMyBatis3JavaMapperType(sb.toString());

        sb.setLength(0);
        sb.append(calculateJavaClientInterfacePackage());
        sb.append('.');
        if (StringUtility.stringHasValue(tableConfiguration.getSqlProviderName())) {
            sb.append(tableConfiguration.getSqlProviderName());
        } else {
            sb.append(fullyQualifiedTable.getDomainObjectName());
            sb.append("SqlProvider"); //$NON-NLS-1$
        }
        setMyBatis3SqlProviderType(sb.toString());

        setAttribute(config, PropertyRegistry.ANY_ROOT_INTERFACE, CorePropertyRegistry.MAPPER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);

        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_PATH, CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_FILES, CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_EXT, CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        // generator
        setAttribute(config, CorePropertyRegistry.ANY_GENERIC_GENERATOR,
                CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_GENERATOR);
    }

    /**
     * 计算Service配置属性
     */
    protected void calculateAttributes() {
        if (!(context instanceof CoreContext)) {
            return;
        }
        CoreContext coreContext = CoreContext.class.cast(context);
        // service
        calcServiceAttributes(coreContext);
        // manager
        calcManagerAttributes(coreContext);
        // controller
        calcControllerAttributes(coreContext);
        // script
        calcScriptAttributes(coreContext);
        // text
        calcTextAttributes(coreContext);
    }

    /**
     * calculate Attributes for Service
     * 
     * @param coreContext
     */
    protected void calcServiceAttributes(CoreContext coreContext) {
        JavaClientGeneratorConfiguration config = coreContext.getServiceGeneratorConfiguration();
        if (config == null) {
            return;
        }
        String interfacePackage = config.getTargetPackage();
        String subPackages = calculateSubPackage(config);
        String modelType = fullyQualifiedTable.getDomainObjectName();

        // interface
        StringBuilder sb = new StringBuilder();
        sb.append(interfacePackage);
        sb.append(subPackages);
        sb.append(Constants.DOT);
        sb.append(modelType);
        sb.append(CorePropertyRegistry.SERVICE);
        setAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_CLASS, sb.toString());
        setAttribute(CorePropertyRegistry.SERVICE + CorePropertyRegistry.IMPL + Constants.DOT + PropertyRegistry.ANY_ROOT_INTERFACE, sb.toString());

        // class
        sb.setLength(0);
        sb.append(interfacePackage);
        sb.append(subPackages);
        sb.append('.');
        sb.append(modelType);
        sb.append(CorePropertyRegistry.SERVICE + CorePropertyRegistry.IMPL);
        setAttribute(CorePropertyRegistry.SERVICE + CorePropertyRegistry.IMPL + Constants.DOT + CorePropertyRegistry.ANY_CLASS, sb.toString());

        // interface
        setAttribute(config, PropertyRegistry.ANY_ROOT_INTERFACE, CorePropertyRegistry.SERVICE + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);

        // implementation
        setAttribute(config, PropertyRegistry.ANY_ROOT_CLASS,
                CorePropertyRegistry.SERVICE + CorePropertyRegistry.IMPL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);

        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_PATH, CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_FILES, CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_EXT, CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        // generator
        setAttribute(config, CorePropertyRegistry.ANY_GENERIC_GENERATOR,
                CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_GENERATOR);
        setAttribute(config, CorePropertyRegistry.ANY_FIELD_GENERATOR, CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_FIELD_GENERATOR);
        setAttribute(config, CorePropertyRegistry.ANY_METHOD_GENERATOR,
                CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_METHOD_GENERATOR);
    }

    /**
     * calculate Attributes for Manager
     * 
     * @param coreContext
     */
    protected void calcManagerAttributes(CoreContext coreContext) {
        JavaClientGeneratorConfiguration config = coreContext.getManagerGeneratorConfiguration();
        if (config == null) {
            return;
        }
        String interfacePackage = config.getTargetPackage();
        String subPackages = calculateSubPackage(config);
        String modelType = fullyQualifiedTable.getDomainObjectName();

        // interface
        StringBuilder sb = new StringBuilder();
        sb.append(interfacePackage);
        sb.append(subPackages);
        sb.append('.');
        sb.append(modelType);
        sb.append(CorePropertyRegistry.MANAGER);
        setAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_CLASS, sb.toString());
        setAttribute(CorePropertyRegistry.MANAGER + CorePropertyRegistry.IMPL + Constants.DOT + PropertyRegistry.ANY_ROOT_INTERFACE, sb.toString());

        // class
        sb.setLength(0);
        sb.append(interfacePackage);
        sb.append(subPackages);
        sb.append('.');
        sb.append(modelType);
        sb.append(CorePropertyRegistry.MANAGER + CorePropertyRegistry.IMPL);
        setAttribute(CorePropertyRegistry.MANAGER + CorePropertyRegistry.IMPL + Constants.DOT + CorePropertyRegistry.ANY_CLASS, sb.toString());

        // interface
        setAttribute(config, PropertyRegistry.ANY_ROOT_INTERFACE, CorePropertyRegistry.MANAGER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);

        // implementation
        setAttribute(config, PropertyRegistry.ANY_ROOT_CLASS,
                CorePropertyRegistry.MANAGER + CorePropertyRegistry.IMPL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);

        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_PATH, CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_FILES, CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_EXT, CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        // generator
        setAttribute(config, CorePropertyRegistry.ANY_GENERIC_GENERATOR,
                CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_GENERATOR);
        setAttribute(config, CorePropertyRegistry.ANY_FIELD_GENERATOR, CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_FIELD_GENERATOR);
        setAttribute(config, CorePropertyRegistry.ANY_METHOD_GENERATOR,
                CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_METHOD_GENERATOR);
    }

    /**
     * calculate Attributes for Controller
     * 
     * @param coreContext
     */
    protected void calcControllerAttributes(CoreContext coreContext) {
        JavaClientGeneratorConfiguration config = coreContext.getControllerGeneratorConfiguration();
        if (config == null) {
            return;
        }
        String classPackage = config.getTargetPackage();
        String subPackages = calculateSubPackage(config);
        String modelType = fullyQualifiedTable.getDomainObjectName();

        // class
        StringBuilder sb = new StringBuilder();
        sb.append(classPackage);
        sb.append(subPackages);
        sb.append('.');
        sb.append(modelType);
        sb.append(CorePropertyRegistry.CONTROLLER);
        setAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_CLASS, sb.toString());

        setAttribute(config, PropertyRegistry.ANY_ROOT_CLASS, CorePropertyRegistry.CONTROLLER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);

        setAttribute(config, CorePropertyRegistry.REST_OVER_CONTROLLER,
                CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.REST_OVER_CONTROLLER);

        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_PATH, CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_FILES,
                CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_EXT, CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);

        // generator
        setAttribute(config, CorePropertyRegistry.ANY_GENERIC_GENERATOR,
                CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_GENERATOR);
        setAttribute(config, CorePropertyRegistry.ANY_FIELD_GENERATOR,
                CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_FIELD_GENERATOR);
        setAttribute(config, CorePropertyRegistry.ANY_METHOD_GENERATOR,
                CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_METHOD_GENERATOR);
    }

    /**
     * calculate Attributes for Script
     * 
     * @param coreContext
     */
    protected void calcScriptAttributes(CoreContext coreContext) {
        JavaClientGeneratorConfiguration config = coreContext.getScriptGeneratorConfiguration();
        if (config == null) {
            return;
        }

        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_PATH, CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_FILES, CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_EXT, CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);

        setAttribute(config, CorePropertyRegistry.ANY_FILE_EXT, CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_FILE_EXT);
        setAttribute(config, PropertyRegistry.ANY_ENABLE_SUB_PACKAGES, CorePropertyRegistry.SCRIPT + Constants.DOT + PropertyRegistry.ANY_ENABLE_SUB_PACKAGES);
        setAttribute(config, CorePropertyRegistry.OVER_TEMPLATE_FILE_NAME, CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.OVER_TEMPLATE_FILE_NAME);
    }

    /**
     * calculate Attributes for Text
     * 
     * @param coreContext
     */
    protected void calcTextAttributes(CoreContext coreContext) {
        JavaClientGeneratorConfiguration config = coreContext.getTextGeneratorConfiguration();
        if (config == null) {
            return;
        }

        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_PATH, CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_FILES, CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_EXT, CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);

        setAttribute(config, CorePropertyRegistry.ANY_FILE_EXT, CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_FILE_EXT);
        setAttribute(config, PropertyRegistry.ANY_ENABLE_SUB_PACKAGES, CorePropertyRegistry.TEXT + Constants.DOT + PropertyRegistry.ANY_ENABLE_SUB_PACKAGES);
        setAttribute(config, CorePropertyRegistry.OVER_TEMPLATE_FILE_NAME, CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.OVER_TEMPLATE_FILE_NAME);
    }

    /**
     * @param generators
     * @param typedPropertyHolder
     * @param warnings
     * @param progressCallback
     */
    protected void calculateGenerators(List<AbstractJavaGenerator> generators, TypedPropertyHolder typedPropertyHolder, List<String> warnings,
            ProgressCallback progressCallback) {
        if (typedPropertyHolder == null) {
            return;
        }
        String type = typedPropertyHolder.getConfigurationType();
        if (StringUtility.stringHasValue(type)) {
            AbstractJavaGenerator abstractGenerator = (AbstractJavaGenerator) ObjectFactory.createInternalObject(type);
            initializeAbstractGenerator(abstractGenerator, warnings, progressCallback);
            generators.add(abstractGenerator);
        }

    }

    @Override
    public void calculateGenerators(List<String> warnings, ProgressCallback progressCallback) {
        if (context instanceof CoreContext) {
            CoreContext coreContext = CoreContext.class.cast(context);
            // service
            calculateGenerators(javaGenerators, coreContext.getServiceGeneratorConfiguration(), warnings, progressCallback);
            // manager
            calculateGenerators(javaGenerators, coreContext.getManagerGeneratorConfiguration(), warnings, progressCallback);
            // controller
            calculateGenerators(javaGenerators, coreContext.getControllerGeneratorConfiguration(), warnings, progressCallback);
            // script
            calculateGenerators(scriptGenerators, coreContext.getScriptGeneratorConfiguration(), warnings, progressCallback);
            // text
            calculateGenerators(textGenerators, coreContext.getTextGeneratorConfiguration(), warnings, progressCallback);
        }
        super.calculateGenerators(warnings, progressCallback);
    }

    @Override
    protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {
        if (getRules().generateExampleClass()) {
            AbstractJavaGenerator javaGenerator = new ExampleGenerator(getExampleProject());
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaGenerators.add(javaGenerator);
        }

        if (getRules().generatePrimaryKeyClass()) {
            AbstractJavaGenerator javaGenerator = new PrimaryKeyGenerator(getModelProject());
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaGenerators.add(javaGenerator);
        }

        if (getRules().generateBaseRecordClass()) {
            AbstractJavaGenerator javaGenerator = createJavaModelGenerator();
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaGenerators.add(javaGenerator);
        }

        if (getRules().generateRecordWithBLOBsClass()) {
            AbstractJavaGenerator javaGenerator = new RecordWithBLOBsGenerator(getModelProject());
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaGenerators.add(javaGenerator);
        }
    }

    /**
     * 根据property配置中的type属性,创建一个Generator
     * 
     * @return
     */
    protected AbstractJavaGenerator createJavaModelGenerator() {
        if (context.getJavaModelGeneratorConfiguration() == null) {
            return null;
        }
        String type = context.getJavaModelGeneratorConfiguration().getProperty("type");
        AbstractJavaGenerator javaGenerator;
        if (StringUtility.stringHasValue(type)) {
            javaGenerator = (AbstractJavaGenerator) ObjectFactory.createInternalObject(type);
        } else {
            javaGenerator = new BaseRecordGenerator(getModelProject());
        }
        return javaGenerator;
    }

    @Override
    public void initialize() {
        super.initialize();
        // 自定义属性计算
        calculateAttributes();
        // load Template Generator
        initTemplateGenerator();
    }

    /**
     * Get files for model
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<GeneratedJavaFile> getGeneratedBaseRecordFiles(Map<String, Object> attrs) {
        List<GeneratedJavaFile> answer = new ArrayList<>();
        String path = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        String templateFiles = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        String ext = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        if (!(StringUtility.stringHasValue(path) && StringUtility.stringHasValue(templateFiles) && StringUtility.stringHasValue(ext))) {
            return answer;
        }

        Map<String, Object> datas = new HashMap<>();
        datas.put(CodeConstants.FILE_COMMENT, CommentsUtils.getFileComment());
        datas.put(CodeConstants.COMMENT, CommentsUtils.getModelComment(this));

        Set<String> imports = new TreeSet<>();
        datas.put(CodeConstants.IMPORTS, imports);
        List<String> interfaces = new ArrayList<>();
        datas.put(CodeConstants.INTERFACES, interfaces);
        List<FieldData> fields = new ArrayList<>();
        datas.put(CodeConstants.FIELDS, fields);
        List<MethodData> methods = new ArrayList<>();
        datas.put(CodeConstants.METHODS, methods);

        boolean serializableImpl = false;
        Class<?> baseClass = TypeUtils.loadClass((String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS));
        // generic types
        if (baseClass != null) {
            ImportUtils.addImportType(imports, baseClass.getName());
            String GenericTypeGeneratorClass = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_GENERATOR);
            if (StringUtility.stringHasValue(GenericTypeGeneratorClass)) {
                GenericTypeGenerator gtGenerator = getGeneratorInstance(
                        ((Class<? extends GenericTypeGenerator>) TypeUtils.loadClass(GenericTypeGeneratorClass)));
                StringBuilder sb = new StringBuilder(baseClass.getSimpleName());
                sb.append(Constants.LESS_THAN).append(gtGenerator.generate(attrs)).append(Constants.GREATER_THAN);
                datas.put(CodeConstants.SUPER_CLASS, sb.toString());
                imports.addAll(gtGenerator.getImports());
            } else {
                datas.put(CodeConstants.SUPER_CLASS, classToSuperClass(baseClass, baseClass.getName(), new FullyQualifiedJavaType(getBaseRecordType()), imports,
                        (Map<String, GenericTypeData>) attrs.get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_GENERIC)));
            }
            serializableImpl = TypeUtils.isSerializable(baseClass);
        }
        if (!serializableImpl) {
            ImportUtils.addImportType(imports, Serializable.class.getName());
            interfaces.add(Serializable.class.getSimpleName());
        }

        // fields
        String fieldGeneratorClass = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_FIELD_GENERATOR);
        FieldGenerator fieldGenerator = (StringUtility.stringHasValue(fieldGeneratorClass)
                ? getGeneratorInstance(((Class<? extends FieldGenerator>) TypeUtils.loadClass(fieldGeneratorClass)))
                : new ModelFieldGenerator());
        fields.addAll(fieldGenerator.generate(attrs));
        imports.addAll(fieldGenerator.getImports());

        // methods
        String methodGeneratorClass = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_METHOD_GENERATOR);
        MethodGenerator methodGenerator = (StringUtility.stringHasValue(methodGeneratorClass)
                ? getGeneratorInstance(((Class<? extends MethodGenerator>) TypeUtils.loadClass(methodGeneratorClass)))
                : new ModelMethodGenerator());
        methods.addAll(methodGenerator.generate(attrs));
        imports.addAll(methodGenerator.getImports());

        String targetPackage = calculateJavaModelPackage();
        String className = fullyQualifiedTable.getDomainObjectName();
        datas.put(CodeConstants.PACKAGE_NAME, targetPackage);
        datas.put(CodeConstants.CLASS_TYPE, CodeConstants.TYPE_CLASS);
        datas.put(CodeConstants.NAME, className);

        // set Date
        setDates(datas);
        setCounter(datas);
        
        answer.add(generatedJavaFiles(datas, path, templateFiles, ext, className + ".java", targetPackage,
                context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        return answer;
    }

    /**
     * Get files for Mapper
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<GeneratedJavaFile> getGeneratedMapperClientFiles(Map<String, Object> attrs) {
        List<GeneratedJavaFile> answer = new ArrayList<>();
        String path = (String) getAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        String templateFiles = (String) getAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        String ext = (String) getAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        if (!(StringUtility.stringHasValue(path) && StringUtility.stringHasValue(templateFiles) && StringUtility.stringHasValue(ext))) {
            return answer;
        }

        String modelBaseClass = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        String modelClass = fullyQualifiedTable.getDomainObjectName();

        Map<String, Object> datas = new HashMap<>();
        datas.put(CodeConstants.FILE_COMMENT, CommentsUtils.getFileComment());
        datas.put(CodeConstants.COMMENT, CommentsUtils.getTypeComment("Mapper for" + modelClass));

        Set<String> imports = new TreeSet<>();
        datas.put(CodeConstants.IMPORTS, imports);

        Class<?> baseInterface = TypeUtils.loadClass((String) getAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS));
        if (baseInterface != null) {
            ImportUtils.addImportType(imports, baseInterface.getName());
            String mapperSuperClass = null;

            String GenericTypeGeneratorClass = (String) getAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_GENERATOR);
            if (StringUtility.stringHasValue(GenericTypeGeneratorClass)) {
                GenericTypeGenerator gtGenerator = getGeneratorInstance(
                        ((Class<? extends GenericTypeGenerator>) TypeUtils.loadClass(GenericTypeGeneratorClass)));
                StringBuilder sb = new StringBuilder(baseInterface.getName());
                sb.append(Constants.LESS_THAN).append(gtGenerator.generate(attrs)).append(Constants.GREATER_THAN);
                
                mapperSuperClass = sb.toString();
                imports.addAll(gtGenerator.getImports());
            } else {
                mapperSuperClass = classToSuperClass(baseInterface, modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()),
                        imports, (Map<String, GenericTypeData>) attrs.get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_GENERIC));

            }
            datas.put(CodeConstants.SUPER_CLASS, mapperSuperClass);
            setAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_ROOT_CLASS, mapperSuperClass);
        }

        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(getMyBatis3JavaMapperType());
        datas.put(CodeConstants.PACKAGE_NAME, mapperType.getPackageName());
        datas.put(CodeConstants.CLASS_TYPE, CodeConstants.TYPE_INTERFACE);
        datas.put(CodeConstants.NAME, mapperType.getShortNameWithoutTypeArguments());

        // set Date
        setDates(datas);
        setCounter(datas);
        
        answer.add(generatedJavaFiles(datas, path, templateFiles, ext, mapperType.getShortNameWithoutTypeArguments() + ".java", mapperType.getPackageName(),
                context.getJavaClientGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));

        return answer;
    }

    /**
     * Get files for service
     * @param attrs
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<GeneratedJavaFile> getGeneratedServiceFiles(Map<String, Object> attrs) {
        List<GeneratedJavaFile> answer = new ArrayList<>();
        if (!(context instanceof CoreContext)) {
            return answer;
        }
        CoreContext coreContext = CoreContext.class.cast(context);

        String path = (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        String templateFiles = (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        String ext = (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        if (!(StringUtility.stringHasValue(path) && StringUtility.stringHasValue(templateFiles) && StringUtility.stringHasValue(ext))) {
            return answer;
        }

        String modelBaseClass = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        String modelClass = fullyQualifiedTable.getDomainObjectName();

        StringBuilder sb = new StringBuilder();
        Map<String, Object> interfaceDatas = new HashMap<>();
        interfaceDatas.put(CodeConstants.FILE_COMMENT, CommentsUtils.getFileComment());
        interfaceDatas.put(CodeConstants.COMMENT, CommentsUtils.getTypeComment("Service Interface for " + modelClass));

        Set<String> interfaceImports = new TreeSet<>();

        Class<?> baseInterface = TypeUtils.loadClass((String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS));
        String GenericTypeGeneratorClass = (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_GENERATOR);
        if (baseInterface != null) {
            ImportUtils.addImportType(interfaceImports, baseInterface.getName());
            String serviceSuperClass = null;
            if (StringUtility.stringHasValue(GenericTypeGeneratorClass)) {
                GenericTypeGenerator gtGenerator = getGeneratorInstance(
                        ((Class<? extends GenericTypeGenerator>) TypeUtils.loadClass(GenericTypeGeneratorClass)));
                sb.setLength(0);
                sb = new StringBuilder(baseInterface.getName());
                sb.append(Constants.LESS_THAN).append(gtGenerator.generate(attrs)).append(Constants.GREATER_THAN);

                serviceSuperClass = sb.toString();
                interfaceImports.addAll(gtGenerator.getImports());
            } else {
                serviceSuperClass = classToSuperClass(baseInterface, modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), interfaceImports,
                        (Map<String, GenericTypeData>) attrs.get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_GENERIC));

            }
            interfaceDatas.put(CodeConstants.SUPER_CLASS, serviceSuperClass);
            setAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_ROOT_CLASS, serviceSuperClass);
        }

        FullyQualifiedJavaType serviceInterface = new FullyQualifiedJavaType(
                (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
        interfaceDatas.put(CodeConstants.PACKAGE_NAME, serviceInterface.getPackageName());
        interfaceDatas.put(CodeConstants.CLASS_TYPE, CodeConstants.TYPE_INTERFACE);
        interfaceDatas.put(CodeConstants.NAME, serviceInterface.getShortNameWithoutTypeArguments());
        interfaceDatas.put(CodeConstants.IMPORTS, interfaceImports);

        Set<String> classImports = new TreeSet<>();
        Map<String, Object> classDatas = new HashMap<>();
        FullyQualifiedJavaType classType = new FullyQualifiedJavaType(
                (String) getAttribute(CorePropertyRegistry.SERVICE + CorePropertyRegistry.IMPL + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
        Class<?> baseClass = TypeUtils
                .loadClass((String) getAttribute(CorePropertyRegistry.SERVICE + CorePropertyRegistry.IMPL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS));

        if (baseClass != null) {
            ImportUtils.addImportType(classImports, baseClass.getName());
            if (StringUtility.stringHasValue(GenericTypeGeneratorClass)) {
                GenericTypeGenerator gtGenerator = getGeneratorInstance(
                        ((Class<? extends GenericTypeGenerator>) TypeUtils.loadClass(GenericTypeGeneratorClass)));
                sb.setLength(0);
                sb.append(baseClass.getSimpleName());
                sb.append(Constants.LESS_THAN).append(gtGenerator.generate(attrs)).append(Constants.GREATER_THAN);
                classDatas.put(CodeConstants.SUPER_CLASS, sb.toString());
                classImports.addAll(gtGenerator.getImports());
            } else {
                classDatas.put(CodeConstants.SUPER_CLASS,
                        classToSuperClass(baseClass, modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), classImports,
                                (Map<String, GenericTypeData>) attrs.get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_GENERIC)));
            }
        }

        // fields
        List<FieldData> fields = new ArrayList<>();
        String fieldGeneratorClass = (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_FIELD_GENERATOR);
        if (StringUtility.stringHasValue(fieldGeneratorClass)) {
            FieldGenerator fieldGenerator = getGeneratorInstance(((Class<? extends FieldGenerator>) TypeUtils.loadClass(fieldGeneratorClass)));
            fields.addAll(fieldGenerator.generate(attrs));
            classImports.addAll(fieldGenerator.getImports());
        } else {
            FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(getMyBatis3JavaMapperType());
            FieldData mapperField = new FieldData();
            List<String> fieldModifiers = new ArrayList<>();
            fieldModifiers.add(Modifier.toString(Modifier.PRIVATE));
            mapperField.setModifiers(fieldModifiers);
            mapperField.setName(StringUtils.uncapitalize(mapperType.getShortNameWithoutTypeArguments()));
            mapperField.setType(mapperType.getShortName());
            fields.add(mapperField);
            ImportUtils.addImportType(classImports, mapperType.getFullyQualifiedNameWithoutTypeParameters());

            List<String> fieldAnnotations = new ArrayList<>();
            fieldAnnotations.add(AnnotationType.RESOURCE.getAnnotation());
            mapperField.setAnnotations(fieldAnnotations);
            ImportUtils.addImportType(classImports, AnnotationType.RESOURCE.getClassName());
        }

        // methods
        List<MethodData> methods = new ArrayList<>();
        classDatas.put(CodeConstants.METHODS, methods);
        String methodGeneratorClass = (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_METHOD_GENERATOR);
        if (StringUtility.stringHasValue(fieldGeneratorClass)) {
            MethodGenerator methodGenerator = getGeneratorInstance(((Class<? extends MethodGenerator>) TypeUtils.loadClass(methodGeneratorClass)));
            methods.addAll(methodGenerator.generate(attrs));
            classImports.addAll(methodGenerator.getImports());
        } else {
            if (baseClass != null) {
                List<Method> methodList = TypeUtils.getAbstractMethods(baseClass);
                if (methodList != null && !methodList.isEmpty()) {
                    String baseMapperInterface = (String) getAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
                    for (Method method : methodList) {
                        // check if return base mapper
                        if (!method.getReturnType().getTypeName().equals(baseMapperInterface)) {
                            continue;
                        }
                        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(getMyBatis3JavaMapperType());
                        ImportUtils.addImportType(classImports, baseMapperInterface);

                        List<String> modifiers = new ArrayList<String>();
                        if (Modifier.isProtected(method.getModifiers())) {
                            modifiers.add(Modifier.toString(Modifier.PROTECTED));
                        } else {
                            modifiers.add(Modifier.toString(Modifier.PUBLIC));
                        }
                        List<String> annotations = new ArrayList<String>();
                        annotations.add(AnnotationType.OVERRIDE.getAnnotation());
                        ImportUtils.addImportType(classImports, AnnotationType.OVERRIDE.getClassName());

                        MethodData md = new MethodData();
                        md.setName(method.getName());
                        md.setModifiers(modifiers);
                        md.setAnnotations(annotations);
                        md.setReturnType((String) getAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_ROOT_CLASS));
                        sb.setLength(0);
                        sb.append("return ").append(StringUtils.uncapitalize(mapperType.getShortNameWithoutTypeArguments())).append(Constants.SEMICOLON);

                        List<String> methodImpl = new ArrayList<>();
                        methodImpl.add(sb.toString());
                        md.setImplementations(methodImpl);

                        methods.add(md);
                    }
                }
            }
        }

        List<String> classAnnotations = new ArrayList<>();
        sb.setLength(0);
        sb.append(AnnotationType.SERVICE.getAnnotation()).append(Constants.PARENTHESES_LEFT).append(Constants.DOUBLE_QUOTATION);
        sb.append(StringUtils.uncapitalize(serviceInterface.getShortNameWithoutTypeArguments()));
        sb.append(Constants.DOUBLE_QUOTATION).append(Constants.PARENTHESES_RIGHT);
        classAnnotations.add(sb.toString());
        classDatas.put(CodeConstants.ANNOTATIONS, classAnnotations);
        ImportUtils.addImportType(classImports, AnnotationType.SERVICE.getClassName());

        List<String> interfaces = new ArrayList<>();
        interfaces.add(serviceInterface.getShortNameWithoutTypeArguments());
        if (!classType.getPackageName().equals(serviceInterface.getPackageName())) {
            ImportUtils.addImportType(classImports, serviceInterface.getFullyQualifiedNameWithoutTypeParameters());
        }

        classDatas.put(CodeConstants.FILE_COMMENT, CommentsUtils.getFileComment());
        classDatas.put(CodeConstants.COMMENT, CommentsUtils.getTypeComment("Service Implementation for " + modelClass));
        classDatas.put(CodeConstants.PACKAGE_NAME, classType.getPackageName());
        classDatas.put(CodeConstants.CLASS_TYPE, CodeConstants.TYPE_CLASS);
        classDatas.put(CodeConstants.NAME, classType.getShortNameWithoutTypeArguments());
        classDatas.put(CodeConstants.IMPORTS, classImports);
        classDatas.put(CodeConstants.FIELDS, fields);
        classDatas.put(CodeConstants.INTERFACES, interfaces);

        // set Date
        setDates(interfaceDatas);
        setDates(classDatas);
        
        setCounter(interfaceDatas);
        setCounter(classDatas);
        
        answer.add(generatedJavaFiles(interfaceDatas, path, templateFiles, ext, serviceInterface.getShortNameWithoutTypeArguments() + ".java",
                serviceInterface.getPackageName(), coreContext.getServiceGeneratorConfiguration().getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        answer.add(generatedJavaFiles(classDatas, path, templateFiles, ext, classType.getShortNameWithoutTypeArguments() + ".java", classType.getPackageName(),
                coreContext.getServiceGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        return answer;
    }

    /**
     * Get files for manager
     * @param attrs
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<GeneratedJavaFile> getGeneratedManagerFiles(Map<String, Object> attrs) {
        List<GeneratedJavaFile> answer = new ArrayList<>();
        if (!(context instanceof CoreContext)) {
            return answer;
        }
        CoreContext coreContext = CoreContext.class.cast(context);

        String path = (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        String templateFiles = (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        String ext = (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        if (!(StringUtility.stringHasValue(path) && StringUtility.stringHasValue(templateFiles) && StringUtility.stringHasValue(ext))) {
            return answer;
        }

        String modelBaseClass = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        String modelClass = fullyQualifiedTable.getDomainObjectName();

        StringBuilder sb = new StringBuilder();
        Map<String, Object> interfaceDatas = new HashMap<>();
        interfaceDatas.put(CodeConstants.FILE_COMMENT, CommentsUtils.getFileComment());
        interfaceDatas.put(CodeConstants.COMMENT, CommentsUtils.getTypeComment("Manager Interface for " + modelClass));

        Set<String> interfaceImports = new TreeSet<>();

        Class<?> baseInterface = TypeUtils.loadClass((String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS));
        String GenericTypeGeneratorClass = (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_GENERATOR);
        if (baseInterface != null) {
            ImportUtils.addImportType(interfaceImports, baseInterface.getName());
            String managerSuperClass = null;
            if (StringUtility.stringHasValue(GenericTypeGeneratorClass)) {
                GenericTypeGenerator gtGenerator = getGeneratorInstance(
                        ((Class<? extends GenericTypeGenerator>) TypeUtils.loadClass(GenericTypeGeneratorClass)));
                sb.setLength(0);
                sb = new StringBuilder(baseInterface.getName());
                sb.append(Constants.LESS_THAN).append(gtGenerator.generate(attrs)).append(Constants.GREATER_THAN);

                managerSuperClass = sb.toString();
                interfaceImports.addAll(gtGenerator.getImports());
            } else {
                managerSuperClass = classToSuperClass(baseInterface, modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), interfaceImports,
                        (Map<String, GenericTypeData>) attrs.get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_GENERIC));

            }
            interfaceDatas.put(CodeConstants.SUPER_CLASS, managerSuperClass);
            setAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_ROOT_CLASS, managerSuperClass);
        }

        FullyQualifiedJavaType managerInterface = new FullyQualifiedJavaType(
                (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
        interfaceDatas.put(CodeConstants.PACKAGE_NAME, managerInterface.getPackageName());
        interfaceDatas.put(CodeConstants.CLASS_TYPE, CodeConstants.TYPE_INTERFACE);
        interfaceDatas.put(CodeConstants.NAME, managerInterface.getShortNameWithoutTypeArguments());
        interfaceDatas.put(CodeConstants.IMPORTS, interfaceImports);

        Set<String> classImports = new TreeSet<>();
        Map<String, Object> classDatas = new HashMap<>();
        FullyQualifiedJavaType classType = new FullyQualifiedJavaType(
                (String) getAttribute(CorePropertyRegistry.MANAGER + CorePropertyRegistry.IMPL + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
        Class<?> baseClass = TypeUtils
                .loadClass((String) getAttribute(CorePropertyRegistry.MANAGER + CorePropertyRegistry.IMPL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS));
        if (baseClass != null) {
            ImportUtils.addImportType(classImports, baseClass.getName());
            if (StringUtility.stringHasValue(GenericTypeGeneratorClass)) {
                GenericTypeGenerator gtGenerator = getGeneratorInstance(
                        ((Class<? extends GenericTypeGenerator>) TypeUtils.loadClass(GenericTypeGeneratorClass)));
                sb.setLength(0);
                sb.append(baseClass.getSimpleName());
                sb.append(Constants.LESS_THAN).append(gtGenerator.generate(attrs)).append(Constants.GREATER_THAN);
                classDatas.put(CodeConstants.SUPER_CLASS, sb.toString());
                classImports.addAll(gtGenerator.getImports());
            } else {
                ImportUtils.addImportType(classImports, baseClass.getName());
                classDatas.put(CodeConstants.SUPER_CLASS,
                        classToSuperClass(baseClass, modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), classImports,
                                (Map<String, GenericTypeData>) attrs.get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_GENERIC)));
            }
        }

        // fields
        List<FieldData> fields = new ArrayList<>();
        String fieldGeneratorClass = (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_FIELD_GENERATOR);
        if (StringUtility.stringHasValue(fieldGeneratorClass)) {
            FieldGenerator fieldGenerator = getGeneratorInstance(((Class<? extends FieldGenerator>) TypeUtils.loadClass(fieldGeneratorClass)));
            fields.addAll(fieldGenerator.generate(attrs));
            classImports.addAll(fieldGenerator.getImports());
        } else {
            FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
                    (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
            FieldData serviceField = new FieldData();
            List<String> fieldModifiers = new ArrayList<>();
            fieldModifiers.add(Modifier.toString(Modifier.PRIVATE));
            serviceField.setModifiers(fieldModifiers);
            serviceField.setName(StringUtils.uncapitalize(serviceType.getShortNameWithoutTypeArguments()));
            serviceField.setType(serviceType.getShortName());
            fields.add(serviceField);
            ImportUtils.addImportType(classImports, serviceType.getFullyQualifiedNameWithoutTypeParameters());
            List<String> fieldAnnotations = new ArrayList<>();
            fieldAnnotations.add(AnnotationType.RESOURCE.getAnnotation());
            serviceField.setAnnotations(fieldAnnotations);
            ImportUtils.addImportType(classImports, AnnotationType.RESOURCE.getClassName());
        }

        // methods
        List<MethodData> methods = new ArrayList<>();
        classDatas.put(CodeConstants.METHODS, methods);
        String methodGeneratorClass = (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_METHOD_GENERATOR);
        if (StringUtility.stringHasValue(fieldGeneratorClass)) {
            MethodGenerator methodGenerator = getGeneratorInstance(((Class<? extends MethodGenerator>) TypeUtils.loadClass(methodGeneratorClass)));
            methods.addAll(methodGenerator.generate(attrs));
            classImports.addAll(methodGenerator.getImports());
        } else {
            if (baseClass != null) {
                List<Method> methodList = TypeUtils.getAbstractMethods(baseClass);
                if (methodList != null && !methodList.isEmpty()) {
                    String baseServiceInterface = (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
                    for (Method method : methodList) {
                        // check if return base service
                        if (!method.getReturnType().getTypeName().equals(baseServiceInterface)) {
                            continue;
                        }
                        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
                                (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
                        ImportUtils.addImportType(classImports, baseServiceInterface);

                        List<String> modifiers = new ArrayList<String>();
                        if (Modifier.isProtected(method.getModifiers())) {
                            modifiers.add(Modifier.toString(Modifier.PROTECTED));
                        } else {
                            modifiers.add(Modifier.toString(Modifier.PUBLIC));
                        }
                        List<String> annotations = new ArrayList<String>();
                        annotations.add(AnnotationType.OVERRIDE.getAnnotation());
                        ImportUtils.addImportType(classImports, AnnotationType.OVERRIDE.getClassName());

                        MethodData md = new MethodData();
                        md.setName(method.getName());
                        md.setModifiers(modifiers);
                        md.setAnnotations(annotations);
                        md.setReturnType((String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_ROOT_CLASS));
                        sb.setLength(0);
                        sb.append("return ").append(StringUtils.uncapitalize(serviceType.getShortNameWithoutTypeArguments())).append(Constants.SEMICOLON);

                        List<String> methodImpl = new ArrayList<>();
                        methodImpl.add(sb.toString());
                        md.setImplementations(methodImpl);

                        methods.add(md);
                    }
                }
                classDatas.put(CodeConstants.METHODS, methods);
            }
        }

        List<String> classAnnotations = new ArrayList<>();
        sb.setLength(0);
        sb.append(AnnotationType.SERVICE.getAnnotation()).append(Constants.PARENTHESES_LEFT).append(Constants.DOUBLE_QUOTATION);
        sb.append(StringUtils.uncapitalize(managerInterface.getShortNameWithoutTypeArguments()));
        sb.append(Constants.DOUBLE_QUOTATION).append(Constants.PARENTHESES_RIGHT);
        classAnnotations.add(sb.toString());
        classDatas.put(CodeConstants.ANNOTATIONS, classAnnotations);
        ImportUtils.addImportType(classImports, AnnotationType.SERVICE.getClassName());

        List<String> interfaces = new ArrayList<>();
        interfaces.add(managerInterface.getShortNameWithoutTypeArguments());
        if (!classType.getPackageName().equals(managerInterface.getPackageName())) {
            ImportUtils.addImportType(classImports, managerInterface.getFullyQualifiedNameWithoutTypeParameters());
        }

        classDatas.put(CodeConstants.FILE_COMMENT, CommentsUtils.getFileComment());
        classDatas.put(CodeConstants.COMMENT, CommentsUtils.getTypeComment("Manager Implementation for " + modelClass));
        classDatas.put(CodeConstants.PACKAGE_NAME, classType.getPackageName());
        classDatas.put(CodeConstants.CLASS_TYPE, CodeConstants.TYPE_CLASS);
        classDatas.put(CodeConstants.NAME, classType.getShortNameWithoutTypeArguments());
        classDatas.put(CodeConstants.IMPORTS, classImports);
        classDatas.put(CodeConstants.FIELDS, fields);
        classDatas.put(CodeConstants.INTERFACES, interfaces);

        // set Date
        setDates(interfaceDatas);
        setDates(classDatas);
        
        setCounter(interfaceDatas);
        setCounter(classDatas);
        
        answer.add(generatedJavaFiles(interfaceDatas, path, templateFiles, ext, managerInterface.getShortNameWithoutTypeArguments() + ".java",
                managerInterface.getPackageName(), coreContext.getManagerGeneratorConfiguration().getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        answer.add(generatedJavaFiles(classDatas, path, templateFiles, ext, classType.getShortNameWithoutTypeArguments() + ".java", classType.getPackageName(),
                coreContext.getManagerGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        return answer;
    }

    /**
     * Get files for controller
     * @param attrs
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<GeneratedJavaFile> getGeneratedControllerFiles(Map<String, Object> attrs) {
        List<GeneratedJavaFile> answer = new ArrayList<>();
        if (!(context instanceof CoreContext)) {
            return answer;
        }
        CoreContext coreContext = CoreContext.class.cast(context);

        String path = (String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        String templateFiles = (String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        String ext = (String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        if (!(StringUtility.stringHasValue(path) && StringUtility.stringHasValue(templateFiles) && StringUtility.stringHasValue(ext))) {
            return answer;
        }
        StringBuilder sb = new StringBuilder();

        String modelBaseClass = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        String modelClass = fullyQualifiedTable.getDomainObjectName();

        Set<String> classImports = new TreeSet<>();
        Map<String, Object> classDatas = new HashMap<>();
        FullyQualifiedJavaType classType = new FullyQualifiedJavaType(
                (String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
        Class<?> baseClass = TypeUtils.loadClass((String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS));
        String GenericTypeGeneratorClass = (String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_GENERATOR);
        if (baseClass != null) {
            ImportUtils.addImportType(classImports, baseClass.getName());
            if (StringUtility.stringHasValue(GenericTypeGeneratorClass)) {
                GenericTypeGenerator gtGenerator = getGeneratorInstance(
                        ((Class<? extends GenericTypeGenerator>) TypeUtils.loadClass(GenericTypeGeneratorClass)));
                sb.setLength(0);
                sb.append(baseClass.getSimpleName());
                sb.append(Constants.LESS_THAN).append(gtGenerator.generate(attrs)).append(Constants.GREATER_THAN);
                classDatas.put(CodeConstants.SUPER_CLASS, sb.toString());
                classImports.addAll(gtGenerator.getImports());
            } else {
                classDatas.put(CodeConstants.SUPER_CLASS,
                        classToSuperClass(baseClass, modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), classImports,
                                (Map<String, GenericTypeData>) attrs.get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_GENERIC)));
            }
        }

        // fields
        List<FieldData> fields = new ArrayList<>();
        String fieldGeneratorClass = (String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_FIELD_GENERATOR);
        if (StringUtility.stringHasValue(fieldGeneratorClass)) {
            FieldGenerator fieldGenerator = getGeneratorInstance(((Class<? extends FieldGenerator>) TypeUtils.loadClass(fieldGeneratorClass)));
            fields.addAll(fieldGenerator.generate(attrs));
            classImports.addAll(fieldGenerator.getImports());
        } else {
            FullyQualifiedJavaType managerType = new FullyQualifiedJavaType(
                    (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
            FieldData managerField = new FieldData();
            List<String> fieldModifiers = new ArrayList<>();
            fieldModifiers.add(Modifier.toString(Modifier.PRIVATE));
            managerField.setModifiers(fieldModifiers);
            managerField.setName(StringUtils.uncapitalize(managerType.getShortNameWithoutTypeArguments()));
            managerField.setType(managerType.getShortName());
            fields.add(managerField);
            ImportUtils.addImportType(classImports, managerType.getFullyQualifiedNameWithoutTypeParameters());
            List<String> fieldAnnotations = new ArrayList<>();
            fieldAnnotations.add(AnnotationType.RESOURCE.getAnnotation());
            managerField.setAnnotations(fieldAnnotations);
            ImportUtils.addImportType(classImports, AnnotationType.RESOURCE.getClassName());
        }

        // methods
        List<MethodData> methods = new ArrayList<>();
        classDatas.put(CodeConstants.METHODS, methods);
        String methodGeneratorClass = (String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_METHOD_GENERATOR);
        if (StringUtility.stringHasValue(fieldGeneratorClass)) {
            MethodGenerator methodGenerator = getGeneratorInstance(((Class<? extends MethodGenerator>) TypeUtils.loadClass(methodGeneratorClass)));
            methods.addAll(methodGenerator.generate(attrs));
            classImports.addAll(methodGenerator.getImports());
        } else {
            if (baseClass != null) {
                List<Method> methodList = TypeUtils.getAbstractMethods(baseClass);
                if (methodList != null && !methodList.isEmpty()) {
                    String baseManagerInterface = (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
                    for (Method method : methodList) {
                        // check if return base manager
                        if (method.getReturnType().getTypeName().equals(baseManagerInterface)) {
                            FullyQualifiedJavaType managerType = new FullyQualifiedJavaType(
                                    (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
                            ImportUtils.addImportType(classImports, baseManagerInterface);
                            List<String> modifiers = new ArrayList<String>();
                            if (Modifier.isProtected(method.getModifiers())) {
                                modifiers.add(Modifier.toString(Modifier.PROTECTED));
                            } else {
                                modifiers.add(Modifier.toString(Modifier.PUBLIC));
                            }
                            List<String> annotations = new ArrayList<String>();
                            annotations.add(AnnotationType.OVERRIDE.getAnnotation());
                            ImportUtils.addImportType(classImports, AnnotationType.OVERRIDE.getClassName());

                            MethodData md = new MethodData();
                            md.setName(method.getName());
                            md.setModifiers(modifiers);
                            md.setAnnotations(annotations);
                            md.setReturnType((String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_ROOT_CLASS));
                            sb.setLength(0);
                            sb.append("return ").append(StringUtils.uncapitalize(managerType.getShortNameWithoutTypeArguments())).append(Constants.SEMICOLON);

                            List<String> methodImpl = new ArrayList<>();
                            methodImpl.add(sb.toString());
                            md.setImplementations(methodImpl);

                            methods.add(md);
                        } else if (method.getReturnType().getTypeName().equals(String.class.getName())) {
                            List<String> modifiers = new ArrayList<String>();
                            if (Modifier.isProtected(method.getModifiers())) {
                                modifiers.add(Modifier.toString(Modifier.PROTECTED));
                            } else {
                                modifiers.add(Modifier.toString(Modifier.PUBLIC));
                            }
                            List<String> annotations = new ArrayList<String>();
                            annotations.add(AnnotationType.OVERRIDE.getAnnotation());
                            ImportUtils.addImportType(classImports, AnnotationType.OVERRIDE.getClassName());

                            MethodData md = new MethodData();
                            md.setName(method.getName());
                            md.setModifiers(modifiers);
                            md.setAnnotations(annotations);
                            md.setReturnType(String.class.getSimpleName());
                            sb.setLength(0);
                            sb.append("return ").append(Constants.DOUBLE_QUOTATION).append(StringUtils.uncapitalize(modelClass))
                                    .append(Constants.DOUBLE_QUOTATION).append(Constants.SEMICOLON);

                            List<String> methodImpl = new ArrayList<>();
                            methodImpl.add(sb.toString());
                            md.setImplementations(methodImpl);

                            methods.add(md);
                        }
                    }
                }
            }
        }

        List<String> classAnnotations = new ArrayList<>();
        sb.setLength(0);
        boolean isRest = StringUtility
                .isTrue((String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.REST_OVER_CONTROLLER));
        if (isRest) {
            sb.append(AnnotationType.RESTCONTROLLER.getAnnotation());
            ImportUtils.addImportType(classImports, AnnotationType.RESTCONTROLLER.getClassName());
        } else {
            sb.append(AnnotationType.CONTROLLER.getAnnotation());
            ImportUtils.addImportType(classImports, AnnotationType.CONTROLLER.getClassName());
        }
        classAnnotations.add(sb.toString());

        sb.setLength(0);
        sb.append(AnnotationType.REQUESTMAPPING.getAnnotation()).append(Constants.PARENTHESES_LEFT).append(Constants.DOUBLE_QUOTATION).append(Constants.SLASH);
        sb.append(fullyQualifiedTable.getIntrospectedTableName().toLowerCase());
        sb.append(Constants.DOUBLE_QUOTATION).append(Constants.PARENTHESES_RIGHT);
        classAnnotations.add(sb.toString());
        ImportUtils.addImportType(classImports, AnnotationType.REQUESTMAPPING.getClassName());

        classDatas.put(CodeConstants.ANNOTATIONS, classAnnotations);

        classDatas.put(CodeConstants.FILE_COMMENT, CommentsUtils.getFileComment());
        classDatas.put(CodeConstants.COMMENT, CommentsUtils.getTypeComment("Controller for " + modelClass));
        classDatas.put(CodeConstants.PACKAGE_NAME, classType.getPackageName());
        classDatas.put(CodeConstants.CLASS_TYPE, CodeConstants.TYPE_CLASS);
        classDatas.put(CodeConstants.NAME, classType.getShortNameWithoutTypeArguments());
        classDatas.put(CodeConstants.IMPORTS, classImports);
        classDatas.put(CodeConstants.FIELDS, fields);

        // set Date
        setDates(classDatas);
        setCounter(classDatas);
        
        answer.add(generatedJavaFiles(classDatas, path, templateFiles, ext, classType.getShortNameWithoutTypeArguments() + ".java", classType.getPackageName(),
                coreContext.getControllerGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        return answer;
    }

    @Override
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        Map<String, Object> attrs = new HashMap<>(attributes);
        attrs.put(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.ANY_CLASS, getBaseRecordType());
        attrs.put(CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_CLASS, getMyBatis3JavaMapperType());
        // Generic and super's fields
        findGenericAndFields((String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS), attrs);
        // table columns
        attrs.put(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.MODEL_COLUMNS, getAllColumns());
        // table name

        List<GeneratedJavaFile> answer = new ArrayList<>();

        List<GeneratedJavaFile> modelFiles = getGeneratedBaseRecordFiles(attrs);
        if (modelFiles != null && !modelFiles.isEmpty()) {
            answer.addAll(modelFiles);
        }

        List<GeneratedJavaFile> mapperFiles = getGeneratedMapperClientFiles(attrs);
        if (mapperFiles != null && !mapperFiles.isEmpty()) {
            answer.addAll(mapperFiles);
        }

        List<GeneratedJavaFile> serviceFiles = getGeneratedServiceFiles(attrs);
        if (serviceFiles != null && !serviceFiles.isEmpty()) {
            answer.addAll(serviceFiles);
        }

        List<GeneratedJavaFile> managerFiles = getGeneratedManagerFiles(attrs);
        if (managerFiles != null && !managerFiles.isEmpty()) {
            answer.addAll(managerFiles);
        }

        List<GeneratedJavaFile> controllerFiles = getGeneratedControllerFiles(attrs);
        if (controllerFiles != null && !controllerFiles.isEmpty()) {
            answer.addAll(controllerFiles);
        }

        return answer;
    }

    /**
     * Get files for script
     * @return
     */
    protected List<GeneratedXmlFile> getGeneratedScriptFiles() {
        List<GeneratedXmlFile> answer = new ArrayList<>();
        if (!(context instanceof CoreContext)) {
            return answer;
        }
        CoreContext coreContext = CoreContext.class.cast(context);

        String path = (String) getAttribute(CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        String templateFiles = (String) getAttribute(CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        String ext = (String) getAttribute(CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        String fileExt = (String) getAttribute(CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_FILE_EXT);
        if (!(StringUtility.stringHasValue(path) && StringUtility.stringHasValue(templateFiles) && StringUtility.stringHasValue(ext))) {
            return answer;
        }

        answer = new ArrayList<>();
        Map<String, Object> datas = new HashMap<>();
        datas.put(CodeConstants.NAME, fullyQualifiedTable.getDomainObjectName().toLowerCase());
        datas.put(CodeConstants.TABLE_NAME, getFullyQualifiedTableNameAtRuntime());
        datas.put(CodeConstants.MODEL_NAME, fullyQualifiedTable.getDomainObjectName());
        datas.put(CodeConstants.MAPPING, fullyQualifiedTable.getIntrospectedTableName().toLowerCase());
        datas.put(CodeConstants.REMARK, getRemarks());
        datas.put(CodeConstants.SUB_PACKAGE_NAME, StringUtils.uncapitalize(fullyQualifiedTable.getDomainObjectName()));

        List<IntrospectedColumn> pkColumns = getPrimaryKeyColumns();
        if (pkColumns != null && !pkColumns.isEmpty()) {
            List<ColumnData> primaryColumns = new ArrayList<>();
            for (IntrospectedColumn column : pkColumns) {
                primaryColumns.add(toColumnData(column));
            }
            datas.put(CodeConstants.PRIMARY_COLUMNS, primaryColumns);
        }
        List<IntrospectedColumn> oColumns = getNonPrimaryKeyColumns();
        if (oColumns != null && !oColumns.isEmpty()) {
            List<ColumnData> columns = new ArrayList<>();
            for (IntrospectedColumn column : oColumns) {
                columns.add(toColumnData(column));
            }
            datas.put(CodeConstants.COLUMNS, columns);
        }
        
        // set Date
        setDates(datas);
        setCounter(datas);

        boolean enableSubPackages = StringUtility.isTrue((String) getAttribute(CorePropertyRegistry.SCRIPT + Constants.DOT + PropertyRegistry.ANY_ENABLE_SUB_PACKAGES));
        boolean overTemplateName = StringUtility.isTrue((String) getAttribute(CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.OVER_TEMPLATE_FILE_NAME));
        
        String tempExt = (StringUtility.stringHasValue(fileExt) ? fileExt : ext);
        List<String> tfs = StringUtils.split(templateFiles);
        for (String s : tfs) {
            if (!StringUtility.stringHasValue(s)) {
                continue;
            }
            answer.add(generatedTextFiles(datas, path, s, ext, (overTemplateName ? fullyQualifiedTable.getDomainObjectName() : s) + "." + tempExt,
                    coreContext.getScriptGeneratorConfiguration().getTargetPackage() + File.separatorChar
                            + (enableSubPackages ? StringUtils.uncapitalize(fullyQualifiedTable.getDomainObjectName()) : ""),
                    coreContext.getScriptGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
            overTemplateName = false;
        }
        return answer;
    }

    /**
     * Get files for text
     * @return
     */
    protected List<GeneratedXmlFile> getGeneratedTextFiles() {

        List<GeneratedXmlFile> answer = new ArrayList<>();
        if (!(context instanceof CoreContext)) {
            return answer;
        }
        CoreContext coreContext = CoreContext.class.cast(context);

        String path = (String) getAttribute(CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        String templateFiles = (String) getAttribute(CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        String ext = (String) getAttribute(CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        String fileExt = (String) getAttribute(CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_FILE_EXT);
        if (!(StringUtility.stringHasValue(path) && StringUtility.stringHasValue(templateFiles) && StringUtility.stringHasValue(ext))) {
            return answer;
        }

        answer = new ArrayList<>();
        Map<String, Object> datas = new HashMap<>();
        datas.put(CodeConstants.NAME, fullyQualifiedTable.getDomainObjectName().toLowerCase());
        datas.put(CodeConstants.TABLE_NAME, getFullyQualifiedTableNameAtRuntime());
        datas.put(CodeConstants.MODEL_NAME, fullyQualifiedTable.getDomainObjectName());
        datas.put(CodeConstants.MAPPING, fullyQualifiedTable.getIntrospectedTableName().toLowerCase());
        datas.put(CodeConstants.REMARK, getRemarks());
        datas.put(CodeConstants.SUB_PACKAGE_NAME, StringUtils.uncapitalize(fullyQualifiedTable.getDomainObjectName()));

        List<IntrospectedColumn> pkColumns = getPrimaryKeyColumns();
        if (pkColumns != null && !pkColumns.isEmpty()) {
            List<ColumnData> primaryColumns = new ArrayList<>();
            for (IntrospectedColumn column : pkColumns) {
                primaryColumns.add(toColumnData(column));
            }
            datas.put(CodeConstants.PRIMARY_COLUMNS, primaryColumns);
        }
        List<IntrospectedColumn> oColumns = getNonPrimaryKeyColumns();
        if (oColumns != null && !oColumns.isEmpty()) {
            List<ColumnData> columns = new ArrayList<>();
            for (IntrospectedColumn column : oColumns) {
                columns.add(toColumnData(column));
            }
            datas.put(CodeConstants.COLUMNS, columns);
        }
        
        // set Date
        setDates(datas);
        setCounter(datas);
        
        boolean enableSubPackages = StringUtility.isTrue((String) getAttribute(CorePropertyRegistry.TEXT + Constants.DOT + PropertyRegistry.ANY_ENABLE_SUB_PACKAGES));
        boolean overTemplateName = StringUtility.isTrue((String) getAttribute(CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.OVER_TEMPLATE_FILE_NAME));

        String tempExt = (StringUtility.stringHasValue(fileExt) ? fileExt : ext);
        List<String> tfs = StringUtils.split(templateFiles);
        for (String s : tfs) {
            if (!StringUtility.stringHasValue(s)) {
                continue;
            }
            answer.add(generatedTextFiles(datas, path, s, ext, (overTemplateName ? fullyQualifiedTable.getDomainObjectName() : s) + "." + tempExt,
                    coreContext.getTextGeneratorConfiguration().getTargetPackage() + File.separatorChar
                            + (enableSubPackages ? StringUtils.uncapitalize(fullyQualifiedTable.getDomainObjectName()) : ""),
                    coreContext.getTextGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
            overTemplateName = false;
        }
        return answer;
    }

    /**
     * 用于select By Primary的方法
     * 
     * @return
     */
    protected String getPrimaryType() {
        String parameterType;
        if (getRules().generatePrimaryKeyClass()) {
            parameterType = getPrimaryKeyType();
        } else {
            // PK fields are in the base class. If more than one PK field, then
            // they are coming in a map.
            if (getPrimaryKeyColumns().size() > 1) {
                parameterType = "map"; //$NON-NLS-1$
            } else {
                parameterType = getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().toString();
            }
        }
        return parameterType;
    }

    /**
     * Get files for mapper's xml
     * 
     * @return
     */
    protected List<GeneratedXmlFile> getGeneratedXMLMapperFiles() {
        List<GeneratedXmlFile> answer = new ArrayList<>();
        String path = (String) getAttribute(CorePropertyRegistry.XML_MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        String templateFiles = (String) getAttribute(CorePropertyRegistry.XML_MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        String ext = (String) getAttribute(CorePropertyRegistry.XML_MAPPER + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);
        if (!(StringUtility.stringHasValue(path) && StringUtility.stringHasValue(templateFiles) && StringUtility.stringHasValue(ext))) {
            return answer;
        }

        answer = new ArrayList<>();
        Map<String, Object> datas = new HashMap<>();
        datas.put(CodeConstants.CLASS_NAME, getMyBatis3SqlMapNamespace());
        datas.put(CodeConstants.TABLE_NAME, getFullyQualifiedTableNameAtRuntime());
        datas.put(CodeConstants.MODEL_NAME, getBaseRecordType());
        datas.put(CodeConstants.RESULT_MAP_ID, getBaseResultMapId());
        datas.put(CodeConstants.COLUMN_SQL_ID, getBaseColumnListId());

        List<IntrospectedColumn> pkColumns = getPrimaryKeyColumns();
        if (pkColumns != null && !pkColumns.isEmpty()) {
            List<ColumnData> primaryColumns = new ArrayList<>();
            for (IntrospectedColumn column : pkColumns) {
                primaryColumns.add(toColumnData(column));
            }
            datas.put(CodeConstants.PRIMARY_COLUMNS, primaryColumns);
        }
        List<IntrospectedColumn> oColumns = getNonPrimaryKeyColumns();
        if (oColumns != null && !oColumns.isEmpty()) {
            List<ColumnData> columns = new ArrayList<>();
            for (IntrospectedColumn column : oColumns) {
                columns.add(toColumnData(column));
            }
            datas.put(CodeConstants.COLUMNS, columns);
        }
        
        // set Date
        setDates(datas);
        setCounter(datas);

        answer.add(generatedTextFiles(datas, path, templateFiles, ext, getMyBatis3XmlMapperFileName(), getMyBatis3XmlMapperPackage(),
                context.getSqlMapGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));

        return answer;
    }

    /**
     * copy data from IntrospectedColumn to ColumnData
     * @param column
     * @return
     */
    protected ColumnData toColumnData(IntrospectedColumn column) {
        ColumnData cd = new ColumnData();
        cd.setColumn(column.getActualColumnName());
        cd.setProperty(column.getJavaProperty());
        cd.setJdbcType(column.getJdbcTypeName());
        cd.setJavaType(column.getFullyQualifiedJavaType().getShortNameWithoutTypeArguments());
        cd.setNullAble(column.isNullable() ? "true" : "false");
        cd.setLength(column.getLength());
        cd.setRemark(column.getRemarks());
        return cd;
    }

    @Override
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        List<GeneratedXmlFile> answer = new ArrayList<>();

        List<GeneratedXmlFile> xmlMapperFiles = getGeneratedXMLMapperFiles();
        if (xmlMapperFiles != null && !xmlMapperFiles.isEmpty()) {
            answer.addAll(xmlMapperFiles);
        }

        List<GeneratedXmlFile> javascriptFiles = getGeneratedScriptFiles();
        if (javascriptFiles != null && !javascriptFiles.isEmpty()) {
            answer.addAll(javascriptFiles);
        }

        List<GeneratedXmlFile> viewFiles = getGeneratedTextFiles();
        if (viewFiles != null && !viewFiles.isEmpty()) {
            answer.addAll(viewFiles);
        }

        return answer;
    }

    /**
     * initialize the template Generator
     */
    @SuppressWarnings("unchecked")
    protected void initTemplateGenerator() {
        templateGeneratorClass = (Class<? extends TemplateGenerator>) TypeUtils.loadClass(context.getProperty(CorePropertyRegistry.TEMPLATE_GENERATOR_IMPL));
    }

    /**
     * check class for Generic Type
     * 
     * @param baseModelClass
     */
    protected String classToSuperClass(Class<?> clazz, String baseModelClass, FullyQualifiedJavaType modelType, Set<String> imports,
            Map<String, GenericTypeData> genericTypes) {
        if (clazz == null) {
            return "";
        }

        TypeVariable<?>[] typeVariables = (TypeVariable<?>[]) clazz.getTypeParameters();
        if (typeVariables == null || typeVariables.length <= 0) {
            return clazz.getSimpleName();
        }
        StringBuilder sb = new StringBuilder();
        Map<String, FullyQualifiedJavaType> gtds = new HashMap<>();
        for (GenericTypeData gtd : genericTypes.values()) {
            if (gtd.getJavaType() == null) {
                continue;
            }
            gtds.put(gtd.getTypeName(), gtd.getJavaType());
        }
        for (TypeVariable<?> typeVar : typeVariables) {
            FullyQualifiedJavaType fqjt = gtds.get(typeVar.getName());
            if (fqjt != null) {
                sb.append(fqjt.getShortName()).append(Constants.COMMA_SPACE);
                ImportUtils.addImportType(imports, fqjt.getFullyQualifiedNameWithoutTypeParameters());
            } else {
                Type[] types = typeVar.getBounds();
                boolean added = false;
                if (types != null && types.length > 0) {
                    for (Type type : types) {
                        if (type instanceof ParameterizedType) {
                            ParameterizedType pt = ParameterizedType.class.cast(type);
                            if (pt.getRawType().getTypeName().equals(baseModelClass)) {
                                sb.append(modelType.getShortNameWithoutTypeArguments()).append(Constants.COMMA_SPACE);
                                added = true;
                                ImportUtils.addImportType(imports, modelType.getFullyQualifiedNameWithoutTypeParameters());
                                break;
                            }
                        }
                    }
                }
                if (!added) {
                    sb.append("String, ");
                }
            }
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
            sb.insert(0, Constants.LESS_THAN);
            sb.append(Constants.GREATER_THAN);
        }
        sb.insert(0, clazz.getSimpleName());
        return sb.toString();
    }

    /**
     * check base model class
     * @param clazz
     * @param fieldNames
     * @param genericTypes
     */
    protected void processBaseModelClass(Class<?> clazz, Set<String> fieldNames, Map<String, GenericTypeData> genericTypes) {
        if (clazz == null || clazz.isInterface()) {
            return;
        }

        Set<String> typeVarNames = new LinkedHashSet<>();
        TypeVariable<?>[] typeVariables = (TypeVariable<?>[]) clazz.getTypeParameters();
        if (typeVariables != null && typeVariables.length > 0) {
            for (TypeVariable<?> typeVar : typeVariables) {
                typeVarNames.add(typeVar.getTypeName());
            }
        }

        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                if (Constants.SERIALVERSIONUID.equals(field.getName())) {
                    continue;
                }
                fieldNames.add(field.getName());
                if (typeVarNames.contains(field.getGenericType().getTypeName())) {
                    GenericTypeData gtd = new GenericTypeData();
                    gtd.setTypeName(field.getGenericType().getTypeName());
                    genericTypes.put(field.getName(), gtd);
                }
            }
        }
        processForSuperClass(clazz.getSuperclass(), fieldNames, typeVarNames, genericTypes);
    }

    /**
     * check for super class for base model class
     * 
     * @param clazz
     * @param fieldNames
     * @param typeVarNames
     * @param genericTypes
     */
    protected void processForSuperClass(Class<?> clazz, Set<String> fieldNames, Set<String> typeVarNames, Map<String, GenericTypeData> genericTypes) {
        if (clazz == null || clazz.isInterface()) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                if (Constants.SERIALVERSIONUID.equals(field.getName())) {
                    continue;
                }
                fieldNames.add(field.getName());
                if (typeVarNames.contains(field.getGenericType().getTypeName())) {
                    GenericTypeData gtd = new GenericTypeData();
                    gtd.setTypeName(field.getGenericType().getTypeName());
                    genericTypes.put(field.getName(), gtd);
                }
            }
        }
        processForSuperClass(clazz.getSuperclass(), fieldNames, typeVarNames, genericTypes);
    }
    
    

    /**
     * Generate Java File
     * 
     * @param datas
     * @param path
     * @param templateFile
     * @param ext
     * @param fileName
     * @param targetPackge
     * @param targetProject
     * @param encoding
     * @return
     */
    protected GeneratedJavaFile generatedJavaFiles(Map<String, Object> datas, String path, String templateFile, String ext, String fileName,
            String targetPackge, String targetProject, String encoding) {
        try {
            TemplateGenerator tg = templateGeneratorClass.getDeclaredConstructor().newInstance();
            tg.setTemplatePath(path);
            tg.setTemplateExt(ext);
            return new TemplateGeneratedJavaFile(tg, templateFile, datas, fileName, targetPackge, targetProject, encoding);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate Text File
     * 
     * @param datas
     * @param path
     * @param templateFile
     * @param ext
     * @param fileName
     * @param targetPackge
     * @param targetProject
     * @param encoding
     * @return
     */
    protected GeneratedXmlFile generatedTextFiles(Map<String, Object> datas, String path, String templateFile, String ext, String fileName, String targetPackge,
            String targetProject, String encoding) {
        try {
            TemplateGenerator tg = templateGeneratorClass.getDeclaredConstructor().newInstance();
            tg.setTemplatePath(path);
            tg.setTemplateExt(ext);
            return new TemplateGeneratedXmlFile(tg, templateFile, datas, fileName, targetPackge, targetProject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param modelSuperClass
     * @param attrs
     */
    protected void findGenericAndFields(String modelSuperClass, Map<String, Object> attrs) {
        if (!StringUtility.stringHasValue(modelSuperClass)) {
            return;
        }
        Class<?> clazz = TypeUtils.loadClass(modelSuperClass);
        if (clazz == null) {
            return;
        }
        Set<String> includedFields = new LinkedHashSet<>();
        Map<String, GenericTypeData> genericTypes = new LinkedHashMap<>();
        processBaseModelClass(clazz, includedFields, genericTypes);

        if (!genericTypes.isEmpty()) {
            // find real types for genericTypes
            List<IntrospectedColumn> columns = getAllColumns();
            if (columns != null && !columns.isEmpty()) {
                for (IntrospectedColumn column : columns) {
                    GenericTypeData gtd = genericTypes.get(column.getJavaProperty());
                    if (gtd == null) {
                        continue;
                    }
                    gtd.setJavaType(column.getFullyQualifiedJavaType());
                }
            }
        }

        attrs.put(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_FIELDS, includedFields);
        attrs.put(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_GENERIC, genericTypes);
    }
    
    /**
     * 实例化 generator
     * @param <G>
     * @param generatorClass
     * @return
     */
    public <G> G getGeneratorInstance(Class<G> generatorClass) {
        try {
            return generatorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * set current dates to datas 
     * 
     * @param datas
     */
    protected void setDates(Map<String, Object> datas) {
        datas.put(CodeConstants.DATE, DateUtils.getDate());
        datas.put(CodeConstants.DATE_TIME, DateUtils.getDateTime());
        datas.put(CodeConstants.CURRENT_DATE, DateUtils.getCurrentDate());
    }
    
    /**
     * set the counter
     * 
     * @param datas
     */
    protected void setCounter(Map<String, Object> datas) {
        datas.put(CodeConstants.COUNTER, new Counter());
    }

}
