/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.poi.write;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.starmoon1617.starmie.utils.doc.enums.DateMode;
import io.github.starmoon1617.starmie.utils.doc.head.DocHead;

/**
 * Utility Class for writing excel
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class ExcelWriter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelWriter.class);
    
    private ExcelWriter() {

    }
    
    /**
     * write data to excel
     * 
     * @param <E>
     * @param os
     * @param sheetName
     * @param heads
     * @param es
     * @param dateMode
     */
    public static <E> void WriteToExcel(OutputStream os, String sheetName, List<DocHead> heads, List<E> es, DateMode dateMode) {
        WorkbookUtil.validateSheetName(sheetName);
        SXSSFWorkbook wb = null;
        try {
            wb = new SXSSFWorkbook(100);
            ExcelWriteHandler<E> ewh = new ExcelWriteHandler<>(wb, sheetName, heads, dateMode);
            ewh.writeDatas(es);
            wb.write(os);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (wb != null) {
                    wb.dispose();
                    wb.close();
                }
            } catch (IOException e) {
                LOGGER.error("inputstream or workbook close exception: {}", e.getMessage());
            }
        }
    }
    
    /**
     * build an ExcelWriteHandler
     * 
     * @param <E>
     * @param sheetName
     * @param heads
     * @param dateMode
     * @return
     */
    public static <E> ExcelWriteHandler<E> buildExcelWriteHandler(String sheetName, List<DocHead> heads, DateMode dateMode) {
        WorkbookUtil.validateSheetName(sheetName);
        return new ExcelWriteHandler<>(new SXSSFWorkbook(100), sheetName, heads, dateMode);
    }
    
    /**
     * write data to excel with given handler
     * @param <E>
     * @param handler
     * @param es
     */
    public static <E> void writeDatas(ExcelWriteHandler<E> handler, List<E> es) {
        if (handler != null) {
            handler.writeDatas(es);
        }
    }
    
    /**
     * flush to workbook
     * @param <E>
     * @param os
     * @param handler
     */
    public static <E> void flush(OutputStream os, ExcelWriteHandler<E> handler) {
        try {
            if (handler == null) {
                return;
            }
            handler.getWorkbook().write(os);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (handler != null) {
                    handler.getWorkbook().dispose();
                    handler.getWorkbook().close();
                }
            } catch (IOException e) {
                LOGGER.error("inputstream or workbook close exception: {}", e.getMessage());
            }
        }
    }
}
