/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.constant;

/**
 * property key for data in template
 * 
 * @date 2023-10-16
 * @author Nathan Liao
 */
public class CodeConstants {
    
    private CodeConstants() {
        
    }
    
    /**
     * XML name space class
     */
    public static String CLASS_NAME = "className";

    /**
     * XML model class name
     */
    public static String MODEL_NAME = "modelName";

    /**
     * result map id name
     */
    public static String RESULT_MAP_ID = "resultMapId";

    /**
     * column sql id
     */
    public static String COLUMN_SQL_ID = "columnSqlId";
    
    /**
     * comment for text, class, field, method
     */
    public static String COMMENT = "comment";
    
    /**
     * table name
     */
    public static String TABLE_NAME = "tableName";
    
    /**
     * Primary column list
     */
    public static String PRIMARY_COLUMNS = "primaryColumns";

    /**
     * column list
     */
    public static String COLUMNS = "columns";
    
    /**
     * class type - interface/class
     */
    public static String CLASS_TYPE = "classType";
    
    /**
     * CLASS_TYPE value for class
     */
    public static String TYPE_CLASS = "class";
    
    /**
     * CLASS_TYPE value for interface
     */
    public static String TYPE_INTERFACE = "interface";
    
    /**
     * name for class, field, method
     */
    public static String NAME = "name";
    
    /**
     * annotations for class, field, method
     */
    public static String ANNOTATIONS = "annotations";
    
    /**
     * comment for file, using to write copyright;
     */
    public static String FILE_COMENT = "fileComent";
    
    /**
     * name for package
     */
    public static String PACKAGE_NAME = "packageName";
    
    /**
     * import list
     */
    public static String IMPORTS = "imports";
    
    
    /**
     * super class name
     */
    public static String SUPER_CLASS = "superClass";
    
    /**
     * interface list
     */
    public static String INTERFACES = "interfaces";
    
    /**
     * fields define for the class
     */
    public static String FIELDS = "fields";
    
    /**
     * methods define for the class
     */
    public static String METHODS = "methods";
    
    /**
     * mapping for controller
     */
    public static String MAPPING = "mapping";

}
