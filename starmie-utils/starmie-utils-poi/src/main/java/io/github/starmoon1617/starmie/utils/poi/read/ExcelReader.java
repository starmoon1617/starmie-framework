/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.poi.read;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ooxml.extractor.POIXMLExtractorFactory;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.starmoon1617.starmie.core.util.EntityUtils;

/**
 * Utility Class for reading excel
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class ExcelReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReader.class);

    static {
        // set EVENT user mode
        POIXMLExtractorFactory.setAllThreadsPreferEventExtractors(true);
    }

    private ExcelReader() {

    }

    /**
     * Use Event User Mode to read an Excel file
     * 
     * @param <T>
     * @param io
     *            - Excel文件流
     * @param fieldNames
     *            - Excel表列对应的属性名称列表
     * @param type
     *            - 转换对象的名称
     * @param erListener
     *            - 数据转换后的处理监听器
     * @return
     * @throws Exception
     */
    public static final <T> List<T> read(InputStream io, List<String> fieldNames, ExcelReadHandler<T> handler, Type type, RowReadListener<T> erListener)
            throws Exception {
        XSSFEventBasedExcelExtractor extractor = null;
        try {
            List<T> datas = new ArrayList<>();
            ExcelReadHandler<T> erh = (handler != null ? handler : new ExcelReadJsonHandler<>(fieldNames, EntityUtils.toJavaType(type), erListener));
            extractor = (XSSFEventBasedExcelExtractor) (new POIXMLExtractorFactory()).create(io, null);
            XSSFReader xssfReader = new XSSFReader(extractor.getPackage());
            SharedStrings strings = new ReadOnlySharedStringsTable(extractor.getPackage());
            StylesTable styles = xssfReader.getStylesTable();
            XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
            while (iter.hasNext()) {
                try (InputStream stream = iter.next()) {
                    extractor.processSheet(erh, styles, null, strings, stream);
                    datas.addAll(erh.getDatas());
                    erh.reset();
                }
            }
            extractor.close();
            return datas;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            LOGGER.error("Excel Read Exception:", e);
            throw e;
        } finally {
            if (extractor != null) {
                try {
                    extractor.close();
                } catch (Exception e) {
                    LOGGER.error("extractor close exception: {}", e.getMessage());
                }
            }
        }

    }

    /**
     * Use Event User Mode to read an Excel file, using default
     * ExcelReadJsonHandler
     * 
     * @param <T>
     * @param io
     *            - Excel文件流
     * @param fieldNames
     *            - Excel表列对应的属性名称列表
     * @param type
     *            - 转换对象的名称
     * @param erListener
     *            - 数据转换后的处理监听器
     * @return
     * @throws Exception
     */
    public static final <T> List<T> read(InputStream io, List<String> fieldNames, Type type, RowReadListener<T> erListener) throws Exception {
        return read(io, fieldNames, null, type, erListener);
    }

    /**
     * Use Event User Mode to read an Excel file 使用Event User
     * Mode方式读取Excel文件,并返回对应类型的列表
     * 
     * @param <T>
     * @param io
     *            - Excel文件流
     * @param fieldNames
     *            - Excel表列对应的属性名称列表
     * @param type
     *            - 转换对象的名称
     * @return
     */
    public static final <T> List<T> read(InputStream io, List<String> fieldNames, Type type) throws Exception {
        return read(io, fieldNames, type, null);
    }

}
