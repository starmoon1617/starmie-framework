/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.poi.read;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

/**
 * Excel Read Handler, define to listen excel read
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public abstract class ExcelReadHandler<T> implements SheetContentsHandler {

    /**
     * field names for excel head
     */
    protected List<String> fieldNames;

    /**
     * if is head row
     */
    protected boolean isHead;

    /**
     * column No. start from 0
     */
    protected int columnNum;

    /**
     * data list read from excel
     */
    protected List<T> datas;

    /**
     * process data after one row read
     */
    protected RowReadListener<T> rowListener;

    /**
     * @param fieldNames
     * @param rowListener
     */
    public ExcelReadHandler(List<String> fieldNames, RowReadListener<T> rowListener) {
        super();
        this.fieldNames = fieldNames;
        this.rowListener = rowListener;
        isHead = false;
        datas = new ArrayList<>();
    }

    /**
     * get data after excel read
     * 
     * @return the datas
     */
    public List<T> getDatas() {
        return datas;
    }

    /**
     * reset data
     */
    public void reset() {
        datas.clear();
    }

    /**
     * function to convert excel row to object data
     * 
     * @return
     */
    protected abstract T rowToData();

    @Override
    public void startRow(int rowNum) {
        // check if head, first row is head
        isHead = (rowNum == 0);
        // reset column No to 0
        columnNum = 0;
    }

    @Override
    public void endRow(int rowNum) {
        // ignore head
        if (isHead) {
            return;
        }
        //
        T data = rowToData();
        if (rowListener != null) {
            // 后置数据处理
            rowListener.postHandle(rowNum, data);
        }
        datas.add(data);
    }

    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        columnNum++;
    }

}
