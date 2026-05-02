/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.app.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utility Class for Session
 * 
 * @date 2023-10-23
 * @author Nathan Liao
 */
public class SessionUtils {

    private SessionUtils() {

    }
    
    /**
     * Get RequestAttributes with null check
     * 
     * @return RequestAttributes or throw IllegalStateException if not available
     */
    private static RequestAttributes getRequestAttributesSafe() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("No request context available");
        }
        return attrs;
    }
    
    /**
     * Validate key parameter
     * 
     * @param key
     */
    private static void validateKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
    }

    /**
     * Set Object to Session
     * 
     * @param session
     * @param key
     * @param object
     */
    public static void set(HttpSession session, String key, Object object) {
        validateKey(key);
        session.setAttribute(key, object);
    }

    /**
     * Set Object to Session with RequestContextHolder
     * 
     * @param key
     * @param object
     */
    public static void set(String key, Object object) {
        validateKey(key);
        getRequestAttributesSafe().setAttribute(key, object, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * Get Object from Session
     * 
     * @param <T>
     * @param session
     * @param key
     * @param clazz
     * @return
     */
    public static <T> T get(HttpSession session, String key, Class<T> clazz) {
        validateKey(key);
        Object obj = session.getAttribute(key);
        return (obj != null && clazz.isInstance(obj)) ? clazz.cast(obj) : null;
    }

    /**
     * Get Object from Session with RequestContextHolder
     * 
     * @param <T>
     * @param key
     * @param clazz
     * @return
     */
    public static <T> T get(String key, Class<T> clazz) {
        validateKey(key);
        Object obj = getRequestAttributesSafe().getAttribute(key, RequestAttributes.SCOPE_SESSION);
        return (obj != null && clazz.isInstance(obj)) ? clazz.cast(obj) : null;
    }

    /**
     * Remove Object from Session
     * 
     * @param session
     * @param key
     */
    public static void clear(HttpSession session, String key) {
        validateKey(key);
        session.removeAttribute(key);
    }

    /**
     * Remove Object from Session with RequestContextHolder
     * 
     * @param key
     */
    public static void clear(String key) {
        validateKey(key);
        getRequestAttributesSafe().removeAttribute(key, RequestAttributes.SCOPE_SESSION);
    }
    
    /**
     * Set Attribute to current request
     * @param name
     * @param object
     */
    public static void setAttr(String name, Object object) {
        validateKey(name);
        getRequestAttributesSafe().setAttribute(name, object, RequestAttributes.SCOPE_REQUEST);
    }
    
    /**
     * Get Object from request with RequestContextHolder
     * 
     * @param <T>
     * @param name
     * @param clazz
     * @return
     */
    public static <T> T getAttr(String name, Class<T> clazz) {
        validateKey(name);
        Object obj = getRequestAttributesSafe().getAttribute(name, RequestAttributes.SCOPE_REQUEST);
        return (obj != null && clazz.isInstance(obj)) ? clazz.cast(obj) : null;
    }
    
    /**
     * Get Object from request
     * 
     * @param <T>
     * @param request
     * @param name
     * @param clazz
     * @return
     */
    public static <T> T getAttr(ServletRequest request, String name, Class<T> clazz) {
        validateKey(name);
        Object obj = request.getAttribute(name);
        return (obj != null && clazz.isInstance(obj)) ? clazz.cast(obj) : null;
    }
}
