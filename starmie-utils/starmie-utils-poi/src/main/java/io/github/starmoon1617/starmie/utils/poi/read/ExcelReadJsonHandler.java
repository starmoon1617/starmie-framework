/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.poi.read;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFComment;

import com.fasterxml.jackson.databind.JavaType;

import io.github.starmoon1617.starmie.core.util.CommonUtils;
import io.github.starmoon1617.starmie.core.util.EntityUtils;

/**
 * Excel read handler implementation using JSON Utility 
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class ExcelReadJsonHandler<T> extends ExcelReadHandler<T> {
    
    /**
     * type of T
     */
    private JavaType type;

    /**
     *  JSON String for one row
     */
    private StringBuilder rowRaw;
    
    /**
     * @param fieldNames
     * @param rowListener
     * @param type
     */
    public ExcelReadJsonHandler(List<String> fieldNames, JavaType type, RowReadListener<T> rowListener) {
        super(fieldNames, rowListener);
        this.type = type;
        rowRaw = new StringBuilder(256);
    }
    
    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        if (isHead) {
            return;
        }
        rowRaw.append("\"").append(fieldNames.get(columnNum)).append("\":");
        if (CommonUtils.isNotBlank(formattedValue)) {
            rowRaw.append("\"").append(formattedValue).append("\",");
        } else {
            rowRaw.append("null,");
        }
        // 列号增加
        columnNum++;
    }
    
    @Override
    public void startRow(int rowNum) {
        super.startRow(rowNum);
        if (isHead) {
            return;
        }
        // reset row raw string
        rowRaw.setLength(0);
        rowRaw.append("{");
    }
    
    @Override
    public void endRow(int rowNum) {
        if (isHead) {
            return;
        }
        // remove last comma (,)
        rowRaw.deleteCharAt(rowRaw.length() - 1);
        rowRaw.append("}");
        super.endRow(rowNum);
    }

    @Override
    protected T rowToData() {
        // use entity Utility
        return EntityUtils.fromJson(rowRaw.toString(), type);
    }

}
