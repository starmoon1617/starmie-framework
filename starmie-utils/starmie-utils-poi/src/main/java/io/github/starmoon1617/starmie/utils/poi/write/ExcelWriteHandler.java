/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.poi.write;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import io.github.starmoon1617.starmie.core.util.CommonUtils;
import io.github.starmoon1617.starmie.core.util.EntityUtils;
import io.github.starmoon1617.starmie.utils.doc.constant.FontConstants;
import io.github.starmoon1617.starmie.utils.doc.convert.Converter;
import io.github.starmoon1617.starmie.utils.doc.enums.DateMode;
import io.github.starmoon1617.starmie.utils.doc.head.DocHead;

/**
 * Excel write handler
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class ExcelWriteHandler<E> {
    
    /**
     * date formatter
     */
    private DateTimeFormatter formatter;

    /**
     * workbook
     */
    private SXSSFWorkbook wb;

    /**
     * work sheet
     */
    private SXSSFSheet sheet;

    /**
     * field names
     */
    private Map<Integer, String> fieldMap;

    /**
     * converters
     */
    private Map<Integer, Converter<?>> convertMap;

    /**
     * if has converter
     */
    private boolean hasConverter;

    /**
     * default cell style
     */
    private CellStyle cellStyle;

    /**
     * current row No
     */
    private int rowNum;

    /**
     * total column 
     */
    private int columnNum;
    
    /**
     * Create a Excel write handler
     * 
     * @param wb
     * @param sheetName
     * @param heads
     * @param dateMode
     */
    ExcelWriteHandler(SXSSFWorkbook wb, String sheetName, List<DocHead> heads, DateMode dateMode) {
        this.wb = wb;
        sheet = wb.createSheet(sheetName);
        fieldMap = new LinkedHashMap<>();
        convertMap = new LinkedHashMap<>();
        hasConverter = false;
        rowNum = 0;
        cellStyle = getStyle(wb, false);
        if (dateMode != null && DateMode.NONE != dateMode) {
            formatter = DateTimeFormatter.ofPattern(dateMode.getPattern()); 
        }
        // init excel head
        writeHeads(heads, getStyle(wb, true));
    }
    
    /**
     * 获取当前的Workbook对象
     * @return
     */
    SXSSFWorkbook getWorkbook() {
        return wb;
    }
    
    /**
     * create style
     * @param wb
     * @param isHead
     * @return
     */
    protected CellStyle getStyle(SXSSFWorkbook wb, boolean isHead) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints(isHead ? (short) 14 : (short) 12);
        font.setFontName(isHead ? FontConstants.SIMHEI : FontConstants.SIMSUN);
        if (isHead) {
            font.setBold(true);
            style.setAlignment(HorizontalAlignment.CENTER);
        }
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }
    
    /**
     * write head to excel
     * @param heads
     * @param headStyle
     */
    void writeHeads(List<DocHead> heads, CellStyle headStyle) {
        columnNum = 0;
        int width = 0;
        Row headRow = sheet.createRow(rowNum);
        for (DocHead eh : heads) {
            // set fields to a Map
            fieldMap.put(columnNum, eh.getField());
            // set converters to a Map
            if (eh.getConverter() != null) {
                convertMap.put(columnNum, eh.getConverter());
                hasConverter = true;
            }
            // set column width
            width = eh.getWidth() != null ? eh.getWidth() : 80;
            sheet.setColumnWidth(columnNum, width * 41);
            // set head and style
            Cell cell = headRow.createCell(columnNum);
            cell.setCellStyle(headStyle);
            cell.setCellValue(eh.getTitle());
            columnNum++;
        }
        rowNum = 1;
    }
    
    /**
     * write data
     * 
     * @param es
     */
    void writeDatas(List<E> es) {
        if (CommonUtils.isEmpty(es)) {
            return;
        }
        int dSize = es.size();
        for (int i = 0; i < dSize; i++) {
            writeRowData(sheet.createRow(rowNum + i), es.get(i));
        }
        rowNum = rowNum + dSize;
    }
    
    /**
     * write data to one row
     * 
     * @param row
     * @param e
     */
    void writeRowData(Row row, E e) {
        if (e instanceof List<?>) {
            writeListData(row, (List<?>) e);
        } else if (e instanceof Map<?, ?>) {
            writeMapData(row, (Map<?, ?>) e);
        } else {
            writeObjectData(row, e);
        }
    }
    
    /**
     * write list to one row
     * 
     * @param row
     * @param list
     * @return
     */
    void writeListData(Row row, List<?> list) {
        for (int i = 0; i < columnNum; i++) {
            // 获取属性值
            convertAndWrite(row.createCell(i), i, list.get(i));
        }
    }

    /**
     * write map to one row
     * 
     * @param row
     * @param map
     */
    void writeMapData(Row row, Map<?, ?> map) {
        for (int i = 0; i < columnNum; i++) {
            // 获取属性值
            Object value = map.get(fieldMap.get(i));
            if (value == null) {
                value = map.get(i);
            }
            convertAndWrite(row.createCell(i), i, value);
        }
    }

    /**
     * write object to one row
     * 
     * @param row
     * @param e
     */
    void writeObjectData(Row row, E e) {
        try {
            for (int i = 0; i < columnNum; i++) {
                // 获取属性值
                convertAndWrite(row.createCell(i), i,  EntityUtils.getValue(e, fieldMap.get(i)));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * convert value and write to cell
     * 
     * @param cell
     * @param columnNum
     * @param value
     */
    @SuppressWarnings("unchecked")
    void convertAndWrite(Cell cell, int columnNum, Object value) {
        cell.setCellStyle(cellStyle);
        if (value == null) {
            cell.setBlank();
            return;
        }
        if (hasConverter) {
            Converter<?> converter = convertMap.get(columnNum);
            if (converter != null) {
                cell.setCellValue(((Converter<Object>) converter).convert(value));
                return;
            }
        }
        if (value instanceof String) {
            cell.setCellValue(String.class.cast(value));
            return;
        }
        if (value instanceof Integer) {
            cell.setCellValue(Double.valueOf(value.toString()));
            return;
        }
        if (value instanceof Long) {
            cell.setCellValue(value.toString());
            return;
        }
        if (value instanceof Boolean) {
            cell.setCellValue(Boolean.class.cast(value));
            return;
        }
        if (value instanceof Date) {
            if (formatter != null) {
                cell.setCellValue(LocalDateTime.ofInstant(Date.class.cast(value).toInstant(), ZoneId.systemDefault()).format(formatter));
            } else {
                cell.setCellValue(Date.class.cast(value));
            }
            return;
        }
        if (value instanceof Double) {
            cell.setCellValue(Double.class.cast(value));
            return;
        }
        if (value instanceof BigDecimal) {
            cell.setCellValue(BigDecimal.class.cast(value).doubleValue());
            return;
        }
        if (value instanceof LocalDate) {
            if (formatter != null) {
                cell.setCellValue(LocalDate.class.cast(value).format(formatter));
            } else {
                cell.setCellValue(LocalDate.class.cast(value));
            }
            return;
        }
        if (value instanceof LocalDateTime) {
            if (formatter != null) {
                cell.setCellValue(LocalDateTime.class.cast(value).format(formatter));
            } else {
                cell.setCellValue(LocalDateTime.class.cast(value));
            }
            return;
        }
        if (value instanceof Calendar) {
            if (formatter != null) {
                cell.setCellValue(LocalDateTime.ofInstant(Calendar.class.cast(value).toInstant(), ZoneId.systemDefault()).format(formatter));
            } else {
                cell.setCellValue(Calendar.class.cast(value));
            }
            return;
        }
        // default write as String
        cell.setCellValue(value.toString());
    }

}
