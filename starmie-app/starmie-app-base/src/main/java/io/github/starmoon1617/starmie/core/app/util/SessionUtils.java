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
     * Set Object to Session
     * 
     * @param session
     * @param key
     * @param object
     */
    public static void set(HttpSession session, String key, Object object) {
        session.setAttribute(key, object);
    }

    /**
     * Set Object to Session with RequestContextHolder
     * 
     * @param key
     * @param object
     */
    public static void set(String key, Object object) {
        RequestContextHolder.getRequestAttributes().setAttribute(key, object, RequestAttributes.SCOPE_SESSION);
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
        Object obj = session.getAttribute(key);
        return (obj != null ? clazz.cast(obj) : null);
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
        Object obj = RequestContextHolder.getRequestAttributes().getAttribute(key, RequestAttributes.SCOPE_SESSION);
        return (obj != null ? clazz.cast(obj) : null);
    }

    /**
     * Remove Object from Session
     * 
     * @param session
     * @param key
     */
    public static void clear(HttpSession session, String key) {
        session.removeAttribute(key);
    }

    /**
     * Remove Object from Session with RequestContextHolder
     * 
     * @param key
     */
    public static void clear(String key) {
        RequestContextHolder.getRequestAttributes().removeAttribute(key, RequestAttributes.SCOPE_SESSION);
    }
    
    /**
     * Set Attribute to current request
     * @param name
     * @param object
     */
    public static void setAttr(String name, Object object) {
        RequestContextHolder.getRequestAttributes().setAttribute(name, object, RequestAttributes.SCOPE_REQUEST);
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
        Object obj = RequestContextHolder.getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_REQUEST);
        return (obj != null ? clazz.cast(obj) : null);
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
        Object obj = request.getAttribute(name);
        return (obj != null ? clazz.cast(obj) : null);
    }
}
