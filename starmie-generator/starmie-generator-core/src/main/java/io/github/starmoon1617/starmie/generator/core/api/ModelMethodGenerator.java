/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.api;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import io.github.starmoon1617.starmie.generator.core.config.CorePropertyRegistry;
import io.github.starmoon1617.starmie.generator.core.constant.Constants;
import io.github.starmoon1617.starmie.generator.core.data.MethodData;
import io.github.starmoon1617.starmie.generator.core.util.CommentsUtils;

/**
 * 
 * 
 * @date 2023-10-27
 * @author Nathan Liao
 */
public class ModelMethodGenerator extends AbstractDataGenerator implements MethodGenerator {

    /**
     * add Getter to method list
     * 
     * @param column
     * @return
     */
    protected void addGetterAndSetter(List<MethodData> methods, IntrospectedColumn column) {
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
        getterData.setComments(CommentsUtils.getGetterComment(column));
        setterData.setComments(CommentsUtils.getSetterComment(column));

        FullyQualifiedJavaType type = column.getFullyQualifiedJavaType();
        // imports
        addImports(type.getFullyQualifiedNameWithoutTypeParameters());

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

    @SuppressWarnings("unchecked")
    @Override
    public List<MethodData> generate(Map<String, Object> attrs) {
        List<MethodData> methods = new ArrayList<>();

        List<IntrospectedColumn> columns = (List<IntrospectedColumn>) attrs
                .get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.MODEL_COLUMNS);
        if (columns == null || columns.isEmpty()) {
            return methods;
        }

        Set<String> superFields = (Set<String>) attrs.get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_FIELDS);
        for (IntrospectedColumn column : columns) {
            if (superFields.contains(column.getJavaProperty())) {
                continue;
            }
            addGetterAndSetter(methods, column);
        }
        return methods;
    }

}
