/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.internal.util.StringUtility;

import io.github.starmoon1617.starmie.generator.core.constant.Constants;

/**
 * Utility Class for Comments
 * 
 * @date 2023-10-16
 * @author Nathan Liao
 */
public class CommentsUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    private CommentsUtils() {

    }

    /**
     * format current time to 'yyyy-MM-dd' string
     * 
     * @return
     */
    public static final String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    /**
     * default comment for a file
     * 
     * @return
     */
    public static String getFileComment() {
        StringBuilder sb = new StringBuilder("/*\n");
        sb.append(" * Copyright (c) ").append(Calendar.getInstance().get(Calendar.YEAR)).append(", Starmoon1617 and/or Nathan Liao. All rights reserved.\n");
        sb.append(" */");
        return sb.toString();
    }

    /**
     * default comment for a Class
     * 
     * @return
     */
    public static String getTypeComment(String comment) {
        StringBuilder sb = new StringBuilder("/**\n");
        if (StringUtility.stringHasValue(comment)) {
            sb.append(comment);
        }
        sb.append(" *\n");
        sb.append(" * Generated by Starmie Generator.\n");
        sb.append(" *\n");
        sb.append(" * @date ");
        sb.append(getCurrentDate());
        sb.append("\n");
        sb.append(" */");
        return sb.toString();
    }

    /**
     * return comment for model class
     * 
     * @return
     */
    public static String getModelComment(IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder("/**\n");
        String remarks = introspectedTable.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for (String remarkLine : remarkLines) {
                sb.append(" *   ");
                sb.append(remarkLine);
                sb.append("\n");
            }
            sb.append(" *\n");
        }
        sb.append(" * Generated by Starmie Generator, corresponds to the database table ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        sb.append("\n");
        sb.append(" *\n");
        sb.append(" * @date ");
        sb.append(getCurrentDate());
        sb.append("\n");
        sb.append(" */");
        return sb.toString();
    }

    /**
     * return field comment for a model
     * 
     * @param introspectedTable
     * @param introspectedColumn
     * @return
     */
    public static String getFieldComment(IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder("/**\n");
        sb.append(" *");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        sb.append(Constants.DOT);
        sb.append(introspectedColumn.getActualColumnName());
        String remarks = introspectedColumn.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for (String remarkLine : remarkLines) {
                sb.append(" ").append(Constants.HYPHEN).append(" ").append(remarkLine);
            }
        }
        sb.append("\n");
        sb.append(" *\n");
        sb.append(" * Generated by Starmie Generator.\n");
        sb.append(" */");
        return sb.toString();
    }

    /**
     * return Getter method comment for a model
     * 
     * @param introspectedTable
     * @param introspectedColumn
     * @return
     */
    public static String getGetterComment(IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder("/**");
        sb.append(" * This method was generated by Starmie Generator.\n");
        sb.append(" * This method returns the value of the database column ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        sb.append(Constants.DOT);
        sb.append(introspectedColumn.getActualColumnName());
        String remarks = introspectedColumn.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for (String remarkLine : remarkLines) {
                sb.append(" ").append(Constants.HYPHEN).append(" ").append(remarkLine);
            }
        }
        sb.append("\n");
        sb.append(" * @return the value of ");
        sb.append(introspectedTable.getFullyQualifiedTable()).append('.').append(introspectedColumn.getActualColumnName()).append("\n");
        sb.append(" */");
        return sb.toString();
    }

    /**
     * return Setter method comment for a model
     * 
     * @param introspectedTable
     * @param introspectedColumn
     * @return
     */
    public static String getSetterComment(IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder("/**");
        sb.append(" * This method was generated by Starmie Generator.\n");
        sb.append(" * This method sets the value of the database column ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        String remarks = introspectedColumn.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for (String remarkLine : remarkLines) {
                sb.append(" ").append(Constants.HYPHEN).append(" ").append(remarkLine);
            }
        }
        sb.append("\n");
        sb.append(" * @param ");
        sb.append(introspectedColumn.getJavaProperty()).append(" the value for ").append(introspectedTable.getFullyQualifiedTable());
        sb.append(Constants.DOT).append(introspectedColumn.getActualColumnName()).append("\n");
        sb.append(" */");
        return sb.toString();
    }

}
