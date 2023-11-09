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
import io.github.starmoon1617.starmie.generator.core.data.FieldData;
import io.github.starmoon1617.starmie.generator.core.util.CommentsUtils;
import io.github.starmoon1617.starmie.generator.core.util.SerialVersionUIDUtils;

/**
 * help to Generate fields for Model class
 * 
 * @date 2023-10-27
 * @author Nathan Liao
 */
public class ModelFieldGenerator extends AbstractDataGenerator implements FieldGenerator {

    /**
     * add field to field list
     * 
     * @param column
     */
    protected void addField(List<FieldData> fields, IntrospectedColumn column) {
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
        addImports(type.getFullyQualifiedNameWithoutTypeParameters());

        // comment
        data.setComments(CommentsUtils.getFieldComment(column));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FieldData> generate(Map<String, Object> attrs) {
        List<FieldData> fields = new ArrayList<>();

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

        List<IntrospectedColumn> columns = (List<IntrospectedColumn>) attrs
                .get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.MODEL_COLUMNS);
        if (columns == null || columns.isEmpty()) {
            return fields;
        }

        Set<String> superFields = (Set<String>) attrs.get(CorePropertyRegistry.MODEL + Constants.DOT + CorePropertyRegistry.SUPER_FIELDS);
        for (IntrospectedColumn column : columns) {
            if (superFields.contains(column.getJavaProperty())) {
                continue;
            }
            addField(fields, column);
        }
        return fields;
    }

}
