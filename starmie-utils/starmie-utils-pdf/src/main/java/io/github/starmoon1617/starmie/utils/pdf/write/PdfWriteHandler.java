/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.pdf.write;

import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import io.github.starmoon1617.starmie.core.constant.TimeConstants;
import io.github.starmoon1617.starmie.core.util.CommonUtils;
import io.github.starmoon1617.starmie.core.util.DateUtils;
import io.github.starmoon1617.starmie.core.util.EntityUtils;
import io.github.starmoon1617.starmie.utils.doc.convert.Converter;
import io.github.starmoon1617.starmie.utils.doc.head.DocHead;
import io.github.starmoon1617.starmie.utils.pdf.config.PageConf;
import io.github.starmoon1617.starmie.utils.pdf.config.SealConf;
import io.github.starmoon1617.starmie.utils.pdf.enums.SealType;
import io.github.starmoon1617.starmie.utils.pdf.font.PdfFonts;

/**
 * PDF write handler
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class PdfWriteHandler<E> {    
    /**
     * 页面配置, page configuration
     */
    private PageConf pageConf;

    /**
     * 签章配置, Seal configuration
     */
    private SealConf sealConf;

    /**
     * pdf document
     */
    private PDDocument pdd;

    /**
     * 文档流列表, Page stream list
     */
    private List<PDPageContentStream> pdpcss;

    /**
     * 属性名称, field names
     */
    private Map<Integer, String> fieldMap;

    /**
     * 转换器名称, converters
     */
    private Map<Integer, Converter<?>> convertMap;

    /**
     * 是否有转换器, if has converter
     */
    private boolean hasConverter;

    /**
     * 宽度列表, column width list
     */
    private List<Float> columnWidths;

    /**
     * 表格宽度, table width
     */
    private float tableWidth;

    /**
     * 当前Y横线的位置, current position Y
     */
    private float currentLineY;

    PdfWriteHandler(PDDocument pdd, String name, List<DocHead> heads, PageConf pageConf, SealConf sealConf) {
        this.pdd = pdd;
        this.pageConf = pageConf;
        this.sealConf = sealConf;
        this.columnWidths = new ArrayList<>();
        fieldMap = new LinkedHashMap<>();
        convertMap = new LinkedHashMap<>();
        hasConverter = false;
        pdpcss = new ArrayList<>();
        // 创建第一页, create first page
        createNewPage(true);
        // 初始化高/宽, init height and width
        initHeadDimensions(heads);
        // 初始化表头, write head titles
        writeHeads(name, heads);
    }

    /**
     * 创建新的一页(A4大小), create a new page
     * @param first
     * @return
     */
    PDPage createNewPage(boolean first) {
        try {
            PDPage page = new PDPage(PdfUtils.getPageRectangle(pageConf));
            pdd.addPage(page);
            PDPageContentStream currentPdcs = new PDPageContentStream(pdd, page);
            // 加入列表, add page to list
            pdpcss.add(currentPdcs);

            currentPdcs.setStrokingColor(Color.BLACK);
            // 当前Y为高减去
            currentLineY = pageConf.getPageHeight() - pageConf.getPagePaddingTop();
            if (first) {
                tableWidth = pageConf.getPageWidth() - pageConf.getPagePaddingLeft() * 2;
            } else {
                // 不是首次才画横线
                writeFirstLine(currentPdcs);
            }
            return page;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化表头尺寸
     * init head
     * 
     * @param heads
     */
    void initHeadDimensions(List<DocHead> heads) {
        // 重新计算各列的宽度
        float totalWidth = 0;
        for (int i = 0; i < heads.size(); i++) {
            DocHead eh = heads.get(i);
            // 设置fieldMap
            fieldMap.put(i, eh.getField());
            // 设置converMap
            if (eh.getConverter() != null) {
                convertMap.put(i, eh.getConverter());
                hasConverter = true;
            }
            float width = eh.getWidth() != null ? eh.getWidth() : 80;
            columnWidths.add(width);
            totalWidth += width;
        }
        if (totalWidth > tableWidth) {
            // 按比例重置
            columnWidths.clear();
            for (DocHead eh : heads) {
                float width = eh.getWidth() != null ? eh.getWidth() : 80;
                columnWidths.add(width * tableWidth / totalWidth);
            }
        } else {
            tableWidth = totalWidth;
        }
    }

    /**
     * 画第一条横线, draw first line
     * 
     * @throws Exception
     */
    void writeFirstLine(PDPageContentStream currentPdcs) throws Exception {
        currentPdcs.moveTo(pageConf.getPagePaddingLeft(), currentLineY);
        currentPdcs.lineTo(pageConf.getPagePaddingLeft() + tableWidth, currentLineY);
        currentPdcs.stroke();
    }

    /**
     * write heads
     * @param name - 名称
     * @param heads - 表头列表
     */
    void writeHeads(String name, List<DocHead> heads) {
        try {
            PDPageContentStream currentPdcs = pdpcss.get(pdpcss.size() - 1);
            writeFirstLine(currentPdcs);
            PdfFonts pdfFonts = pageConf.getFonts();
            float headFontHeight = pdfFonts.getHeadFontHeight();
            float headFontSize = pdfFonts.getHeadFontSize();
            PDFont headFont = pdfFonts.getHeadFont();
            currentLineY = currentLineY - headFontHeight;
            // 写入第二条横线
            currentPdcs.moveTo(pageConf.getPagePaddingLeft(), currentLineY);
            currentPdcs.lineTo(pageConf.getPagePaddingLeft() + tableWidth, currentLineY);
            // 写入左右两边竖线
            currentPdcs.moveTo(pageConf.getPagePaddingLeft(), currentLineY);
            currentPdcs.lineTo(pageConf.getPagePaddingLeft(), currentLineY + headFontHeight);
            currentPdcs.moveTo(pageConf.getPagePaddingLeft() + tableWidth, currentLineY);
            currentPdcs.lineTo(pageConf.getPagePaddingLeft() + tableWidth, currentLineY + headFontHeight);
            currentPdcs.stroke();

            // 写入标题
            currentPdcs.beginText();
            currentPdcs.setFont(headFont, headFontSize);
            // 计算文字宽度
            float titleWidth = headFont.getStringWidth(name) / 1000 * headFontSize;
            float padding = (tableWidth - titleWidth) / 2;
            currentPdcs.newLineAtOffset(pageConf.getPagePaddingLeft() + padding, currentLineY + headFontHeight / 3);
            currentPdcs.showText(name);
            currentPdcs.endText();

            // 写入表头
            float titleHeight = headFontHeight;
            float startX = pageConf.getPagePaddingLeft();
            for (int i = 0; i < heads.size(); i++) {
                float columnWidth = columnWidths.get(i);
                DocHead eh = heads.get(i);
                currentPdcs.beginText();
                currentPdcs.setFont(headFont, headFontSize);
                // 计算文字宽度
                titleWidth = headFont.getStringWidth(eh.getTitle()) / 1000 * headFontSize;
                if (titleWidth <= columnWidth - pageConf.getTextPadding() * 2) {
                    padding = (columnWidth - titleWidth) / 2;
                    currentPdcs.newLineAtOffset(startX + padding, currentLineY - headFontHeight + headFontHeight / 3);
                    currentPdcs.showText(eh.getTitle());
                } else {
                    List<String> multiLine = splitToMultiLine(eh.getTitle(), columnWidth - pageConf.getTextPadding() * 2, headFont, headFontSize);
                    float tmpHeight = headFontHeight * multiLine.size();
                    int add = 1;
                    for (String lineTitle : multiLine) {
                        float lineTitleWidth = headFont.getStringWidth(lineTitle) / 1000 * headFontSize;
                        padding = (columnWidth - lineTitleWidth) / 2;
                        currentPdcs.newLineAtOffset(startX + padding, currentLineY - headFontHeight * add + headFontHeight / 3);
                        currentPdcs.showText(eh.getTitle());
                        currentPdcs.endText();
                        add++;
                        currentPdcs.beginText();
                        currentPdcs.setFont(headFont, headFontSize);
                    }
                    if (tmpHeight > titleHeight) {
                        titleHeight = tmpHeight;
                    }
                }
                currentPdcs.endText();
                startX = startX + columnWidth;
            }

            // 写入竖线
            currentLineY = currentLineY - titleHeight;
            startX = pageConf.getPagePaddingLeft();
            currentPdcs.moveTo(startX, currentLineY);
            currentPdcs.lineTo(startX, currentLineY + titleHeight);
            for (int i = 0; i < columnWidths.size(); i++) {
                startX = startX + columnWidths.get(i);
                currentPdcs.moveTo(startX, currentLineY);
                currentPdcs.lineTo(startX, currentLineY + titleHeight);
            }
            // 写入横线
            currentPdcs.moveTo(pageConf.getPagePaddingLeft(), currentLineY);
            currentPdcs.lineTo(startX, currentLineY);
            currentPdcs.stroke();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * convert value to string
     * 将数据转换成String
     * 
     * @param columnNum
     * @param value
     */
    @SuppressWarnings("unchecked")
    String convert(int columnNum, Object value) {
        if (value == null) {
            return null;
        }
        if (hasConverter) {
            Converter<?> converter = convertMap.get(columnNum);
            if (converter != null) {
                return ((Converter<Object>) converter).convert(value);
            }
        }
        // 按数据类型写入cell
        if (value instanceof String) {
            return String.class.cast(value);
        }
        if (value instanceof Date) {
            return DateUtils.format(TimeConstants.DATE_TIME_FORMAT, Date.class.cast(value));
        }
        if (value instanceof Calendar) {
            return DateUtils.format(TimeConstants.DATE_TIME_FORMAT, Calendar.class.cast(value).getTime());
        }
        if (value instanceof LocalDate) {
            return LocalDate.class.cast(value).format(DateUtils.DATE_TIME_FORMATTER);
        }
        if (value instanceof LocalDateTime) {
            return LocalDateTime.class.cast(value).format(DateUtils.DATE_TIME_FORMATTER);
        }
        // 默认为String
        return value.toString();
    }

    /**
     * 将数据转换成多行
     * 
     * @param text
     * @param paddingWidth
     * @param font
     * @param fontSize
     * @return
     */
    List<String> splitToMultiLine(String text, float paddingWidth, PDFont font, float fontSize) {
        try {
            List<String> multiLine = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            // 算出倍数
            for (int index = 0; index < text.length(); index++) {
                sb.append(text.charAt(index));
                float sbWidth = font.getStringWidth(sb.toString()) / 1000 * fontSize;
                if (sbWidth > paddingWidth) {
                    sb.deleteCharAt(sb.length() - 1);
                    multiLine.add(sb.toString());
                    sb.setLength(0);
                    sb.append(text.charAt(index));
                }
            }
            multiLine.add(sb.toString());
            sb.setLength(0);
            return multiLine;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 写入数据
     * 
     * @param datas
     */
    void writeDatas(List<E> datas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        int dSize = datas.size();
        // 转换后的字符串值
        List<String> textDatas = new ArrayList<>();
        // 转换后多行的字符串值
        Map<Integer, List<String>> multiLineMap = new LinkedHashMap<>();
        for (int i = 0; i < dSize; i++) {
            float lineHeight = convertRowData(datas.get(i), textDatas, multiLineMap);
            writeDatas(textDatas, multiLineMap, lineHeight);
            // 清理已写入的数据
            textDatas.clear();
            multiLineMap.clear();
        }
    }

    /**
     * 写入数据
     * 
     * @param textDatas
     * @param multiLineMap
     * @param lineHeight
     */
    void writeDatas(List<String> textDatas, Map<Integer, List<String>> multiLineMap, float lineHeight) {
        currentLineY = currentLineY - lineHeight;
        if (currentLineY < pageConf.getPagePaddingTop()) {
            flush();
            // 需要换新的一页
            createNewPage(false);
            currentLineY = currentLineY - lineHeight;
        }
        float upperLineY = currentLineY + lineHeight;
        PDPageContentStream currentPdcs = pdpcss.get(pdpcss.size() - 1);
        // 写入数据
        PdfFonts pdfFonts = pageConf.getFonts();
        PDFont font = pdfFonts.getTextFont();
        float fontSize = pdfFonts.getTextFontSize();
        float fontHeight = pdfFonts.getTextFontHeight();
        float startX = pageConf.getPagePaddingLeft();
        boolean hasMultiLine = !multiLineMap.isEmpty();
        try {
            for (int i = 0; i < columnWidths.size(); i++) {
                // 先画竖线
                currentPdcs.moveTo(startX, currentLineY);
                currentPdcs.lineTo(startX, upperLineY);
                currentPdcs.stroke();
                // 写入内容
                String text = textDatas.get(i);
                if (!CommonUtils.isNotBlank(text)) {
                    continue;
                }
                float columnWidth = columnWidths.get(i);

                if (hasMultiLine && multiLineMap.containsKey(i)) {
                    int add = 1;
                    for (String s : multiLineMap.get(i)) {
                        currentPdcs.beginText();
                        currentPdcs.setFont(font, fontSize);
                        currentPdcs.newLineAtOffset(startX + pageConf.getTextPadding(), upperLineY - fontHeight * add + fontHeight / 3);
                        currentPdcs.showText(s);
                        currentPdcs.endText();
                        add++;
                    }
                } else {
                    currentPdcs.beginText();
                    currentPdcs.setFont(font, fontSize);
                    currentPdcs.newLineAtOffset(startX + pageConf.getTextPadding(), upperLineY - fontHeight + fontHeight / 3);
                    currentPdcs.showText(text);
                    currentPdcs.endText();
                }
                startX = startX + columnWidth;
            }
            // 最后的竖线
            currentPdcs.moveTo(startX, currentLineY);
            currentPdcs.lineTo(startX, upperLineY);
            // 最后横线
            currentPdcs.moveTo(pageConf.getPagePaddingLeft(), currentLineY);
            currentPdcs.lineTo(startX, currentLineY);
            currentPdcs.stroke();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换一行数据,并返回最大的行高
     * 
     * @param data
     * @param textDatas
     * @param multiLineMap
     * @return
     */
    float convertRowData(E data, final List<String> textDatas, final Map<Integer, List<String>> multiLineMap) {
        List<?> lData = null;
        Map<?, ?> mData = null;
        if (data instanceof List<?>) {
            lData = (List<?>) data;
        } else if (data instanceof Map<?, ?>) {
            // map处理
            mData = (Map<?, ?>) data;
        }
        PdfFonts pdfFonts = pageConf.getFonts();
        PDFont font = pdfFonts.getTextFont();
        float fontSize = pdfFonts.getTextFontSize();
        int multi = 1;
        try {
            for (int i = 0; i < columnWidths.size(); i++) {
                float columnPaddingWidth = columnWidths.get(i) - pageConf.getTextPadding() * 2;
                Object value = null;
                if (lData != null) {
                    value = getValueFromList(lData, i);
                } else if (mData != null) {
                    value = getValueFromMap(mData, i);
                } else {
                    value = getValueFromObject(data, i);
                }
                String text = convert(i, value);
                textDatas.add(text);
                if (!CommonUtils.isNotBlank(text)) {
                    continue;
                }
                // 获取属性值
                float textWidth = font.getStringWidth(text) / 1000 * fontSize;
                if (textWidth <= columnPaddingWidth) {
                    continue;
                }
                List<String> mLine = splitToMultiLine(text, columnPaddingWidth, font, fontSize);
                multiLineMap.put(i, mLine);
                int tmpM = mLine.size();
                if (tmpM > multi) {
                    multi = tmpM;
                }
            }
            return pdfFonts.getTextFontHeight() * multi;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从List获取对应下标的值
     * 
     * @param data
     * @param columnNum
     * @return
     */
    Object getValueFromList(List<?> data, int columnNum) {
        return data.get(columnNum);
    }

    /**
     * 从Map获取对应key的值
     * @param data
     * @param columnNum
     * @return
     */
    Object getValueFromMap(Map<?, ?> data, int columnNum) {
        Object value = data.get(fieldMap.get(columnNum));
        if (value == null) {
            value = data.get(columnNum);
        }
        return value;
    }

    /**
     * 从data获取对应字段的值
     * @param data
     * @param columnNum
     * @return
     */
    Object getValueFromObject(E data, int columnNum) {
        return EntityUtils.getValue(data, fieldMap.get(columnNum));
    }

    /**
     * 获取当前PDF文档对象
     * @return
     */
    PDDocument getPDDocument() {
        return pdd;
    }

    /**
     * 写入分页
     * @param currentPage
     * @param totalPage
     * @param currentPdcs
     * @throws Exception
     */
    private void writePagination(int currentPage, int totalPage, PDPageContentStream currentPdcs) throws Exception {
        // 底部空白中间位置
        String text = String.format(pageConf.getPaginationText(), currentPage, totalPage);
        PdfFonts pdfFonts = pageConf.getFonts();
        PDFont font = pdfFonts.getTextFont();
        float fontSize = pdfFonts.getTextFontSize();
        float fontHeight = pdfFonts.getTextFontHeight();
        float fontFixedHeight = pdfFonts.getTextFixedHeight();

        // 写入标题
        currentPdcs.beginText();
        currentPdcs.setFont(font, fontSize);
        // 计算文字宽度
        float titleWidth = font.getStringWidth(text) / 1000 * fontSize;
        float padding = (pageConf.getPageWidth() - titleWidth) / 2;
        currentPdcs.newLineAtOffset(padding, pageConf.getPagePaddingTop() - fontHeight + fontFixedHeight);
        currentPdcs.showText(text);
        currentPdcs.endText();
    }

    /**
     * 将数据写入到文档中
     * 
     */
    private void flush() {
        try {
            if (!CommonUtils.isEmpty(pdpcss)) {
                List<SealConf> scList = null;
                int size = pdpcss.size();
                boolean hasSeal = (sealConf != null && SealType.NONE != sealConf.getType());
                for (int i = 0; i < size; i++) {
                    PDPageContentStream pdcs = pdpcss.get(i);
                    if (pageConf.isEnablePagination()) {
                        writePagination(i + 1, size, pdcs);
                    }
                    if (hasSeal) {
                        if (sealConf.getType() == SealType.CROSS_PAGE) {
                            if (scList == null) {
                                scList = PdfUtils.adjustCrossPage(sealConf, size, 0, pageConf.getPageHeight() / 5, pageConf.getPageWidth(),
                                        pageConf.getPageHeight());
                            }
                            PdfUtils.addSeal(scList.get(i), pdd, pdcs);
                        } else {
                            if (i == 0) {
                                // 调整位置
                                PdfUtils.adjustPos(sealConf, pageConf.getPagePaddingLeft() + tableWidth - sealConf.getImage().getWidth() / 2,
                                        pageConf.getPageHeight() - pageConf.getPagePaddingTop() - sealConf.getImage().getHeight() / 2, pageConf.getPageWidth(),
                                        pageConf.getPageHeight());
                                PdfUtils.addSeal(sealConf, pdd, pdcs);
                            }
                            if (sealConf.getType() == SealType.ALL_PAGE) {
                                PdfUtils.addSeal(sealConf, pdd, pdcs);
                            }
                        }
                    }
                    pdcs.close();
                }

            }
        } catch (Exception e) {

        }
    }

    /**
     * 关闭文档流
     */
    void close() {
        flush();
    }
}
