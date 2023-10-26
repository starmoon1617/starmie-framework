/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.mybatis3;

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
import io.github.starmoon1617.starmie.generator.core.util.CommentsUtils;
import io.github.starmoon1617.starmie.generator.core.util.ImportUtils;
import io.github.starmoon1617.starmie.generator.core.util.SerialVersionUIDUtils;
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
     * <fieldName, generic Type>
     */
    private Map<String, GenericTypeData> genericFields = new LinkedHashMap<>();

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
        String classPackage = config.getTargetPackage();
        String subPackages = calculateSubPackage(config);

        // class
        StringBuilder sb = new StringBuilder();
        sb.append(classPackage);
        sb.append(subPackages);
        setAttribute(CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_TARGET_PACKAGE, sb.toString());

        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_PATH, CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_FILES, CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_EXT, CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);

        setAttribute(config, CorePropertyRegistry.ANY_FILE_EXT, CorePropertyRegistry.SCRIPT + Constants.DOT + CorePropertyRegistry.ANY_FILE_EXT);
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
        String classPackage = config.getTargetPackage();
        String subPackages = calculateSubPackage(config);

        // class
        StringBuilder sb = new StringBuilder();
        sb.append(classPackage);
        sb.append(subPackages);
        setAttribute(CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_TARGET_PACKAGE, sb.toString());

        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_PATH, CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_PATH);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_FILES, CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_FILES);
        setAttribute(config, CorePropertyRegistry.ANY_TEMPLATE_EXT, CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_TEMPLATE_EXT);

        setAttribute(config, CorePropertyRegistry.ANY_FILE_EXT, CorePropertyRegistry.TEXT + Constants.DOT + CorePropertyRegistry.ANY_FILE_EXT);
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
     * add field to field list
     * 
     * @param column
     * @return
     */
    protected void addField(Set<String> imports, List<FieldData> fields, IntrospectedColumn column) {
        FieldData data = new FieldData();
        fields.add(data);

        // modifiers
        List<String> modifiers = new ArrayList<>();
        modifiers.add(Modifier.toString(Modifier.PRIVATE));
        data.setModifiers(modifiers);

        // name
        data.setName(column.getJavaProperty());

        // type
        FullyQualifiedJavaType type = column.getFullyQualifiedJavaType();
        data.setType(type.getShortName());

        // imports
        ImportUtils.addImportType(imports, type.getFullyQualifiedNameWithoutTypeParameters());

        // comment
        data.setComments(CommentsUtils.getFieldComment(this, column));
    }

    /**
     * add Getter to method list
     * 
     * @param column
     * @return
     */
    protected void addGetterAndSetter(Set<String> imports, List<MethodData> methods, IntrospectedColumn column) {
        // get fieldName
        String tempName = column.getJavaProperty();
        char firstChar = Character.toUpperCase(tempName.charAt(0));
        if (tempName.length() > 1) {
            tempName = tempName.substring(1);
        } else {
            tempName = "";
        }

        MethodData getterData = new MethodData();
        methods.add(getterData);
        MethodData setterData = new MethodData();
        methods.add(setterData);

        // name
        StringBuilder sb = new StringBuilder("get");
        sb.append(firstChar).append(tempName);
        getterData.setName(sb.toString());
        sb.setLength(0);
        sb.append("set").append(firstChar).append(tempName);
        setterData.setName(sb.toString());

        // modifiers
        List<String> modifiers = new ArrayList<>();
        modifiers.add(Modifier.toString(Modifier.PUBLIC));
        getterData.setModifiers(modifiers);
        setterData.setModifiers(modifiers);

        // comment
        getterData.setComments(CommentsUtils.getGetterComment(this, column));
        setterData.setComments(CommentsUtils.getSetterComment(this, column));

        FullyQualifiedJavaType type = column.getFullyQualifiedJavaType();
        // imports
        ImportUtils.addImportType(imports, type.getFullyQualifiedNameWithoutTypeParameters());

        // return type for getter
        getterData.setReturnType(type.getShortName());

        // return type for setter
        setterData.setReturnType("void");
        // parameter for setter
        List<String> parameters = new ArrayList<>();
        parameters.add(type.getShortName() + " " + column.getJavaProperty());
        setterData.setParameters(parameters);

        // implementation for method
        sb.setLength(0);
        sb.append("return ").append(column.getJavaProperty()).append(Constants.SEMICOLON);
        List<String> getterImpl = new ArrayList<>();
        getterImpl.add(sb.toString());
        getterData.setImplementations(getterImpl);

        sb.setLength(0);
        sb.append("this. ").append(column.getJavaProperty()).append(" = ").append(column.getJavaProperty()).append(Constants.SEMICOLON);
        List<String> setterImpl = new ArrayList<>();
        setterImpl.add(sb.toString());
        setterData.setImplementations(setterImpl);
    }

    /**
     * 获取生成的model代码
     * 
     * @return
     */
    protected List<GeneratedJavaFile> getGeneratedBaseRecordFiles() {
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

        Set<String> includedFields = new LinkedHashSet<>();

        String rootClass = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        Class<?> baseClass = null;
        boolean serializableImpl = false;
        if (StringUtility.stringHasValue(rootClass)) {
            baseClass = TypeUtils.loadClass(rootClass);
            if (baseClass != null) {
                imports.add(baseClass.getName());
                // check serializableImpl
                serializableImpl = checkSerializable(baseClass);
                processBaseModelClass(baseClass, includedFields, genericFields);
            }
        }
        if (!serializableImpl) {
            ImportUtils.addImportType(imports, Serializable.class.getName());
            interfaces.add(Serializable.class.getSimpleName());
        }

        List<String> uIdModifiers = new ArrayList<>();
        uIdModifiers.add(Modifier.toString(Modifier.PRIVATE));
        uIdModifiers.add(Modifier.toString(Modifier.STATIC));
        uIdModifiers.add(Modifier.toString(Modifier.FINAL));
        FieldData uIdFd = new FieldData();
        uIdFd.setName(Constants.SERIALVERSIONUID);
        uIdFd.setModifiers(uIdModifiers);
        uIdFd.setType("long");
        uIdFd.setValue(SerialVersionUIDUtils.getSVUId());
        fields.add(uIdFd);

        List<IntrospectedColumn> pkColumns = getPrimaryKeyColumns();
        if (pkColumns != null && !pkColumns.isEmpty()) {
            for (IntrospectedColumn column : pkColumns) {
                if (genericFields.containsKey(column.getJavaProperty())) {
                    // generic Fields
                    column.getFullyQualifiedJavaType();
                    GenericTypeData gtd = genericFields.get(column.getJavaProperty());
                    gtd.setJavaType(column.getFullyQualifiedJavaType());
                }
                if (includedFields.contains(column.getJavaProperty())) {
                    continue;
                }
                addField(imports, fields, column);
                addGetterAndSetter(imports, methods, column);
            }
        }

        List<IntrospectedColumn> columns = getNonPrimaryKeyColumns();
        if (columns != null && !columns.isEmpty()) {
            for (IntrospectedColumn column : columns) {
                if (genericFields.containsKey(column.getJavaProperty())) {
                    // generic Fields
                    column.getFullyQualifiedJavaType();
                    GenericTypeData gtd = genericFields.get(column.getJavaProperty());
                    gtd.setJavaType(column.getFullyQualifiedJavaType());
                }
                if (includedFields.contains(column.getJavaProperty())) {
                    continue;
                }
                addField(imports, fields, column);
                addGetterAndSetter(imports, methods, column);
            }
        }

        String targetPackage = calculateJavaModelPackage();
        String className = fullyQualifiedTable.getDomainObjectName();
        datas.put(CodeConstants.PACKAGE_NAME, targetPackage);
        datas.put(CodeConstants.CLASS_TYPE, CodeConstants.TYPE_CLASS);
        datas.put(CodeConstants.NAME, className);

        if (baseClass != null) {
            datas.put(CodeConstants.SUPER_CLASS, classToSuperClass(baseClass, baseClass.getName(), new FullyQualifiedJavaType(getBaseRecordType()), imports));
        }

        answer.add(generatedJavaFiles(datas, path, templateFiles, ext, className + ".java", targetPackage,
                context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        return answer;
    }

    /**
     * 获取生成的Mapper代码
     * 
     * @return
     */
    protected List<GeneratedJavaFile> getGeneratedMapperClientFiles() {
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

        String baseInterface = (String) getAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        if (StringUtility.stringHasValue(baseInterface)) {
            ImportUtils.addImportType(imports, baseInterface);
            String mapperSuperClass = classToSuperClass(TypeUtils.loadClass(baseInterface), modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), imports);
            datas.put(CodeConstants.SUPER_CLASS, mapperSuperClass);
            setAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + CorePropertyRegistry.ANY_GENERIC_ROOT_CLASS, mapperSuperClass);
        }

        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(getMyBatis3JavaMapperType());
        datas.put(CodeConstants.PACKAGE_NAME, mapperType.getPackageName());
        datas.put(CodeConstants.CLASS_TYPE, CodeConstants.TYPE_INTERFACE);
        datas.put(CodeConstants.NAME, mapperType.getShortNameWithoutTypeArguments());

        answer.add(generatedJavaFiles(datas, path, templateFiles, ext, mapperType.getShortNameWithoutTypeArguments() + ".java", mapperType.getPackageName(),
                context.getJavaClientGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));

        return answer;
    }

    protected List<GeneratedJavaFile> getGeneratedServiceFiles() {
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

        String baseInterface = (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        if (StringUtility.stringHasValue(baseInterface)) {
            ImportUtils.addImportType(interfaceImports, baseInterface);
            String serviceSuperClass = classToSuperClass(TypeUtils.loadClass(baseInterface), modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), interfaceImports);
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
        String baseClass = (String) getAttribute(CorePropertyRegistry.SERVICE + CorePropertyRegistry.IMPL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        if (StringUtility.stringHasValue(baseClass)) {
            FullyQualifiedJavaType baseClassType = new FullyQualifiedJavaType(baseClass);
            ImportUtils.addImportType(classImports, baseClassType.getFullyQualifiedNameWithoutTypeParameters());
            classDatas.put(CodeConstants.SUPER_CLASS, classToSuperClass(TypeUtils.loadClass(baseClass), modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), classImports));
        }

        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(getMyBatis3JavaMapperType());
        List<FieldData> fields = new ArrayList<>();
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

        List<String> classAnnotations = new ArrayList<>();
        sb.setLength(0);
        sb.append(AnnotationType.SERVICE.getAnnotation()).append(Constants.PARENTHESES_LEFT).append(Constants.DOUBLE_QUOTATION);
        sb.append(StringUtils.uncapitalize(serviceInterface.getShortNameWithoutTypeArguments()));
        sb.append(Constants.DOUBLE_QUOTATION).append(Constants.PARENTHESES_RIGHT);
        classAnnotations.add(sb.toString());
        classDatas.put(CodeConstants.ANNOTATIONS, classAnnotations);
        ImportUtils.addImportType(classImports, AnnotationType.SERVICE.getClassName());

        if (StringUtility.stringHasValue(baseClass)) {
            List<MethodData> methods = new ArrayList<>();
            List<Method> methodList = TypeUtils.getAbstractMethods(TypeUtils.loadClass(baseClass));
            if (methodList != null && !methodList.isEmpty()) {
                String baseMapperInterface = (String) getAttribute(CorePropertyRegistry.MAPPER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
                for (Method method : methodList) {
                    // check if return base mapper
                    if (!method.getReturnType().getTypeName().equals(baseMapperInterface)) {
                        continue;
                    }
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
                    sb.append("return ").append(mapperField.getName()).append(Constants.SEMICOLON);

                    List<String> methodImpl = new ArrayList<>();
                    methodImpl.add(sb.toString());
                    md.setImplementations(methodImpl);

                    methods.add(md);
                }
            }
            classDatas.put(CodeConstants.METHODS, methods);
        }

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

        answer.add(generatedJavaFiles(interfaceDatas, path, templateFiles, ext, serviceInterface.getShortNameWithoutTypeArguments() + ".java",
                serviceInterface.getPackageName(), coreContext.getServiceGeneratorConfiguration().getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        answer.add(generatedJavaFiles(classDatas, path, templateFiles, ext, classType.getShortNameWithoutTypeArguments() + ".java", classType.getPackageName(),
                coreContext.getServiceGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        return answer;
    }

    protected List<GeneratedJavaFile> getGeneratedManagerFiles() {
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

        String baseInterface = (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        if (StringUtility.stringHasValue(baseInterface)) {
            ImportUtils.addImportType(interfaceImports, baseInterface);
            String managerSuperClass = classToSuperClass(TypeUtils.loadClass(baseInterface), modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), interfaceImports);
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
        String baseClass = (String) getAttribute(CorePropertyRegistry.MANAGER + CorePropertyRegistry.IMPL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        if (StringUtility.stringHasValue(baseClass)) {
            FullyQualifiedJavaType baseClassType = new FullyQualifiedJavaType(baseClass);
            ImportUtils.addImportType(classImports, baseClassType.getFullyQualifiedNameWithoutTypeParameters());
            classDatas.put(CodeConstants.SUPER_CLASS, classToSuperClass(TypeUtils.loadClass(baseClass), modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), classImports));
        }

        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
                (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
        List<FieldData> fields = new ArrayList<>();
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

        List<String> classAnnotations = new ArrayList<>();
        sb.setLength(0);
        sb.append(AnnotationType.SERVICE.getAnnotation()).append(Constants.PARENTHESES_LEFT).append(Constants.DOUBLE_QUOTATION);
        sb.append(StringUtils.uncapitalize(managerInterface.getShortNameWithoutTypeArguments()));
        sb.append(Constants.DOUBLE_QUOTATION).append(Constants.PARENTHESES_RIGHT);
        classAnnotations.add(sb.toString());
        classDatas.put(CodeConstants.ANNOTATIONS, classAnnotations);
        ImportUtils.addImportType(classImports, AnnotationType.SERVICE.getClassName());

        if (StringUtility.stringHasValue(baseClass)) {
            List<MethodData> methods = new ArrayList<>();
            List<Method> methodList = TypeUtils.getAbstractMethods(TypeUtils.loadClass(baseClass));
            if (methodList != null && !methodList.isEmpty()) {
                String baseServiceInterface = (String) getAttribute(CorePropertyRegistry.SERVICE + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
                for (Method method : methodList) {
                    // check if return base service
                    if (!method.getReturnType().getTypeName().equals(baseServiceInterface)) {
                        continue;
                    }
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
                    sb.append("return ").append(serviceField.getName()).append(Constants.SEMICOLON);

                    List<String> methodImpl = new ArrayList<>();
                    methodImpl.add(sb.toString());
                    md.setImplementations(methodImpl);

                    methods.add(md);
                }
            }
            classDatas.put(CodeConstants.METHODS, methods);
        }

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

        answer.add(generatedJavaFiles(interfaceDatas, path, templateFiles, ext, managerInterface.getShortNameWithoutTypeArguments() + ".java",
                managerInterface.getPackageName(), coreContext.getManagerGeneratorConfiguration().getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        answer.add(generatedJavaFiles(classDatas, path, templateFiles, ext, classType.getShortNameWithoutTypeArguments() + ".java", classType.getPackageName(),
                coreContext.getManagerGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        return answer;
    }

    protected List<GeneratedJavaFile> getGeneratedControllerFiles() {
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

        String modelBaseClass = (String) getAttribute(CorePropertyRegistry.MODEL + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        String modelClass = fullyQualifiedTable.getDomainObjectName();

        Set<String> classImports = new TreeSet<>();
        Map<String, Object> classDatas = new HashMap<>();
        FullyQualifiedJavaType classType = new FullyQualifiedJavaType(
                (String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
        String baseClass = (String) getAttribute(CorePropertyRegistry.CONTROLLER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
        if (StringUtility.stringHasValue(baseClass)) {
            FullyQualifiedJavaType baseClassType = new FullyQualifiedJavaType(baseClass);
            ImportUtils.addImportType(classImports, baseClassType.getFullyQualifiedNameWithoutTypeParameters());
            classDatas.put(CodeConstants.SUPER_CLASS, classToSuperClass(TypeUtils.loadClass(baseClass), modelBaseClass, new FullyQualifiedJavaType(getBaseRecordType()), classImports));
        }

        FullyQualifiedJavaType managerType = new FullyQualifiedJavaType(
                (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + CorePropertyRegistry.ANY_CLASS));
        List<FieldData> fields = new ArrayList<>();
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

        StringBuilder sb = new StringBuilder();
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

        if (StringUtility.stringHasValue(baseClass)) {
            List<MethodData> methods = new ArrayList<>();
            List<Method> methodList = TypeUtils.getAbstractMethods(TypeUtils.loadClass(baseClass));
            if (methodList != null && !methodList.isEmpty()) {
                String baseManagerInterface = (String) getAttribute(CorePropertyRegistry.MANAGER + Constants.DOT + PropertyRegistry.ANY_ROOT_CLASS);
                for (Method method : methodList) {
                    // check if return base manager
                    if (method.getReturnType().getTypeName().equals(baseManagerInterface)) {
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
                        sb.append("return ").append(managerField.getName()).append(Constants.SEMICOLON);

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
                        sb.append("return ").append(Constants.DOUBLE_QUOTATION).append(StringUtils.uncapitalize(modelClass)).append(Constants.DOUBLE_QUOTATION).append(Constants.SEMICOLON);

                        List<String> methodImpl = new ArrayList<>();
                        methodImpl.add(sb.toString());
                        md.setImplementations(methodImpl);

                        methods.add(md);
                    }
                }
            }
            classDatas.put(CodeConstants.METHODS, methods);
        }

        classDatas.put(CodeConstants.FILE_COMMENT, CommentsUtils.getFileComment());
        classDatas.put(CodeConstants.COMMENT, CommentsUtils.getTypeComment("Controller for " + modelClass));
        classDatas.put(CodeConstants.PACKAGE_NAME, classType.getPackageName());
        classDatas.put(CodeConstants.CLASS_TYPE, CodeConstants.TYPE_CLASS);
        classDatas.put(CodeConstants.NAME, classType.getShortNameWithoutTypeArguments());
        classDatas.put(CodeConstants.IMPORTS, classImports);
        classDatas.put(CodeConstants.FIELDS, fields);

        answer.add(generatedJavaFiles(classDatas, path, templateFiles, ext, classType.getShortNameWithoutTypeArguments() + ".java", classType.getPackageName(),
                coreContext.getControllerGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        return answer;
    }

    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        List<GeneratedJavaFile> answer = new ArrayList<>();

        List<GeneratedJavaFile> modelFiles = getGeneratedBaseRecordFiles();
        if (modelFiles != null && !modelFiles.isEmpty()) {
            answer.addAll(modelFiles);
        }

        List<GeneratedJavaFile> mapperFiles = getGeneratedMapperClientFiles();
        if (mapperFiles != null && !mapperFiles.isEmpty()) {
            answer.addAll(mapperFiles);
        }

        List<GeneratedJavaFile> serviceFiles = getGeneratedServiceFiles();
        if (serviceFiles != null && !serviceFiles.isEmpty()) {
            answer.addAll(serviceFiles);
        }

        List<GeneratedJavaFile> managerFiles = getGeneratedManagerFiles();
        if (managerFiles != null && !managerFiles.isEmpty()) {
            answer.addAll(managerFiles);
        }

        List<GeneratedJavaFile> controllerFiles = getGeneratedControllerFiles();
        if (controllerFiles != null && !controllerFiles.isEmpty()) {
            answer.addAll(controllerFiles);
        }

        return answer;
    }

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

        String tempExt = (StringUtility.stringHasValue(fileExt) ? fileExt : ext);
        List<String> tfs = StringUtils.split(templateFiles);
        for (String s : tfs) {
            if (!StringUtility.stringHasValue(s)) {
                continue;
            }
            answer.add(generatedTextFiles(datas, path, templateFiles, ext, s + "." + tempExt, coreContext.getScriptGeneratorConfiguration().getTargetPackage(),
                    coreContext.getScriptGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
        }
        return answer;
    }

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

        String tempExt = (StringUtility.stringHasValue(fileExt) ? fileExt : ext);
        List<String> tfs = StringUtils.split(templateFiles);
        for (String s : tfs) {
            if (!StringUtility.stringHasValue(s)) {
                continue;
            }
            answer.add(generatedTextFiles(datas, path, templateFiles, ext, s + "." + tempExt, coreContext.getTextGeneratorConfiguration().getTargetPackage(),
                    coreContext.getTextGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));
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
     * 获取生成的XML Mapper 文件
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

        answer.add(generatedTextFiles(datas, path, templateFiles, ext, getMyBatis3XmlMapperFileName(), getMyBatis3XmlMapperPackage(),
                context.getSqlMapGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING)));

        return answer;
    }

    protected ColumnData toColumnData(IntrospectedColumn column) {
        ColumnData cd = new ColumnData();
        cd.setColumn(column.getActualColumnName());
        cd.setProperty(column.getJavaProperty());
        cd.setJdbcType(column.getJdbcTypeName());
        cd.setJavaType(column.getFullyQualifiedJavaType().getShortNameWithoutTypeArguments());
        cd.setNullAble(column.isNullable() ? "true" : "false");
        cd.setLength(column.getLength());
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

    @SuppressWarnings("unchecked")
    protected void initTemplateGenerator() {
        templateGeneratorClass = (Class<? extends TemplateGenerator>) TypeUtils.loadClass(context.getProperty(CorePropertyRegistry.TEMPLATE_GENERATOR_IMPL));
    }

    /**
     * check class for Generic Type
     * 
     * @param baseModelClass
     */
    protected String classToSuperClass(Class<?> clazz, String baseModelClass, FullyQualifiedJavaType modelType, Set<String> imports) {
        if (clazz == null) {
            return "";
        }

        TypeVariable<?>[] typeVariables = (TypeVariable<?>[]) clazz.getTypeParameters();
        if (typeVariables == null || typeVariables.length <= 0) {
            return clazz.getSimpleName();
        }
        StringBuilder sb = new StringBuilder();
        Map<String, FullyQualifiedJavaType> gtds = new HashMap<>();
        for (GenericTypeData gtd : genericFields.values()) {
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
     * 
     * @param baseModelClass
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
     * check if impl Serializable interface
     * 
     * @param clazz
     * @return
     */
    protected boolean checkSerializable(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null && interfaces.length > 0) {
            for (Class<?> i : interfaces) {
                if (Serializable.class.equals(i)) {
                    return true;
                }
            }
        }
        // check super class
        return checkSerializable(clazz.getSuperclass());
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

}
