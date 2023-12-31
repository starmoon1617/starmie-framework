/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.starmoon1617.starmie.core.constant.TimeConstants;
import io.github.starmoon1617.starmie.core.exception.EntityOperationException;
import io.github.starmoon1617.starmie.core.json.DateJsonDeserializer;

/**
 * 实体工具类 
 * Utility Class for Entity
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public class EntityUtils {

    /**
     * exclude fields for bean copy
     */
    private static final String EXCLUDE_ID = "id";

    /**
     * ObjectMapper instance will show null value
     */
    private static final ObjectMapper NOM = new ObjectMapper();

    /**
     * ObjectMapper instance will not show null value
     */
    private static final ObjectMapper NOTNOM = new ObjectMapper();

    /**
     * if ObjectMappers initialized
     */
    private static Boolean omInited = Boolean.FALSE;

    static {
        initOm();
    }

    /**
     * initialize ObjectMappers
     */
    private static synchronized void initOm() {
        if (!omInited.booleanValue()) {

            NOM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            NOM.setDateFormat(new SimpleDateFormat(TimeConstants.DATE_TIME_FORMAT));
            NOM.registerModule((new SimpleModule()).addDeserializer(Date.class, new DateJsonDeserializer()));

            NOTNOM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            NOTNOM.setDateFormat(new SimpleDateFormat(TimeConstants.DATE_TIME_FORMAT));
            NOTNOM.setDefaultPropertyInclusion(Include.NON_NULL);
            NOTNOM.registerModule((new SimpleModule()).addDeserializer(Date.class, new DateJsonDeserializer()));
            omInited = Boolean.TRUE;
        }

    }

    private EntityUtils() {

    }

    /**
     * copy props from target to source, exclude ID
     * 
     * @param target
     * @param source
     */
    public static void copyProperties(Object target, Object source) {
        BeanUtils.copyProperties(source, target, EXCLUDE_ID);
    }

    /**
     * 通过反射获取属性值,没有该属性着返回NULL 
     * get Field value using Reflection
     * 
     * @param target
     * @param fieldName
     * @return
     */
    public static Object getValue(Object target, String fieldName) {
        if (target == null || !CommonUtils.isNotBlank(fieldName)) {
            return null;
        }
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        try {
            return field.get(target);
        } catch (Exception e) {
            throw new EntityOperationException("Get field value Exception!", e);
        }
    }

    /**
     * 通过反射设置属性值,没有该属性则忽略 
     * set Field value using Reflection
     * 
     * @param target
     * @param fieldName
     * @param value
     */
    public static void setValue(Object target, String fieldName, Object value) {
        if (target == null || !CommonUtils.isNotBlank(fieldName)) {
            return;
        }
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        if (field == null) {
            return;
        }
        field.setAccessible(true);
        try {
            field.set(target, value);
        } catch (Exception e) {
            throw new EntityOperationException("Set field value Exception!", e);
        }
    }

    /**
     * Object to JSON String
     * 
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        try {
            return NOM.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EntityOperationException("Object to Json Exception!", e);
        }
    }

    /**
     * Object to JSON String, Exclude Null fields
     * 
     * @param object
     * @return
     */
    public static String toNonNJson(Object object) {
        try {
            return NOM.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EntityOperationException("Object to Json Exception!", e);
        }
    }

    /**
     * 从JSON字符串转成对象
     * JSON String to Object 
     * 
     * @param <T>
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Class<T> type) {
        try {
            return NOM.readValue(str, type);
        } catch (JsonProcessingException e) {
            throw new EntityOperationException("Josn to Object Exception!", e);
        }

    }

    /**
     * 从JSON字符串转成对象
     * JSON String to Object 
     * 
     * @param <T>
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, JavaType type) {
        try {
            return NOM.readValue(str, type);
        } catch (JsonProcessingException e) {
            throw new EntityOperationException("Josn to Object Exception!", e);
        }
    }

    /**
     * 从JSON字符串转成对象
     * JSON String to Object 
     * 
     * @param <T>
     * @param str
     * @param type
     * @return
     * @throws IOException
     */
    public static <T> T fromJson(String str, TypeReference<T> type) throws IOException {
        try {
            return NOM.readValue(str, type);
        } catch (JsonProcessingException e) {
            throw new EntityOperationException("Josn to Object Exception!", e);
        }
    }
    
    /**
     * 将JSON转成List对象
     * JSON String to Object List
     * 
     * @param <T>
     * @param str
     * @param type
     * @return
     */
    public static <T> List<T> fromJsonToList(String str, Class<T> type) {
        try {
            return NOM.readValue(str, NOM.getTypeFactory().constructParametricType(ArrayList.class, type));
        } catch (JsonProcessingException e) {
            throw new EntityOperationException("Josn to Object Exception!", e);
        }
    }
    
    /**
     * From Type to JavaType
     * 
     * @param type
     * @return
     */
    public static JavaType toJavaType(Type type) {
        return NOM.constructType(type);
    }

}
