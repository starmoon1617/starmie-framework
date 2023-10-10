/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.pdf.config;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import io.github.starmoon1617.starmie.utils.pdf.enums.PageOrientation;
import io.github.starmoon1617.starmie.utils.pdf.enums.PageType;
import io.github.starmoon1617.starmie.utils.pdf.font.PdfFonts;

/**
 * PDF page configuration
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class PageConf {
    
    /**
     * 40
     */
    private int pagePaddingTop = 40;

    /**
     * 40
     */
    private int pagePaddingLeft = 40;

    /**
     * padding
     * 字距
     */
    private int textPadding = 2;

    /**
     * DPI, default 72
     */
    private int ppi = 72;

    /**
     * 页面文档类型
     */
    private PageType pageType = PageType.A4;

    /**
     * 方向
     */
    private PageOrientation orientation = PageOrientation.PORTRAIT;

    /**
     * 字体
     */
    private PdfFonts fonts;

    /**
     * 页面
     */
    private PDRectangle pdRectangle;

    /**
     * 页面宽度
     */
    private float pageWidth;

    /**
     * 页面高度
     */
    private float pageHeight;

    /**
     * 是否启用分页
     */
    private boolean enablePagination = true;

    /**
     * 分页文本
     */
    private String paginationText = "第 %s 页 / 共 %s 页";

    /**
     * @return the pagePaddingTop
     */
    public int getPagePaddingTop() {
        return pagePaddingTop;
    }

    /**
     * @param pagePaddingTop the pagePaddingTop to set
     */
    public void setPagePaddingTop(int pagePaddingTop) {
        this.pagePaddingTop = pagePaddingTop;
    }

    /**
     * @return the pagePaddingLeft
     */
    public int getPagePaddingLeft() {
        return pagePaddingLeft;
    }

    /**
     * @param pagePaddingLeft the pagePaddingLeft to set
     */
    public void setPagePaddingLeft(int pagePaddingLeft) {
        this.pagePaddingLeft = pagePaddingLeft;
    }

    /**
     * @return the textPadding
     */
    public int getTextPadding() {
        return textPadding;
    }

    /**
     * @param textPadding the textPadding to set
     */
    public void setTextPadding(int textPadding) {
        this.textPadding = textPadding;
    }

    /**
     * @return the ppi
     */
    public int getPpi() {
        return ppi;
    }

    /**
     * @param ppi the ppi to set
     */
    public void setPpi(int ppi) {
        this.ppi = ppi;
    }

    /**
     * @return the pageType
     */
    public PageType getPageType() {
        return pageType;
    }

    /**
     * @param pageType the pageType to set
     */
    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }

    /**
     * @return the orientation
     */
    public PageOrientation getOrientation() {
        return orientation;
    }

    /**
     * @param orientation the orientation to set
     */
    public void setOrientation(PageOrientation orientation) {
        this.orientation = orientation;
    }

    /**
     * @return the fonts
     */
    public PdfFonts getFonts() {
        return fonts;
    }

    /**
     * @param fonts the fonts to set
     */
    public void setFonts(PdfFonts fonts) {
        this.fonts = fonts;
    }

    /**
     * @return the pdRectangle
     */
    public PDRectangle getPdRectangle() {
        return pdRectangle;
    }

    /**
     * @param pdRectangle the pdRectangle to set
     */
    public void setPdRectangle(PDRectangle pdRectangle) {
        this.pdRectangle = pdRectangle;
    }

    /**
     * @return the pageWidth
     */
    public float getPageWidth() {
        return pageWidth;
    }

    /**
     * @param pageWidth the pageWidth to set
     */
    public void setPageWidth(float pageWidth) {
        this.pageWidth = pageWidth;
    }

    /**
     * @return the pageHeight
     */
    public float getPageHeight() {
        return pageHeight;
    }

    /**
     * @param pageHeight the pageHeight to set
     */
    public void setPageHeight(float pageHeight) {
        this.pageHeight = pageHeight;
    }

    /**
     * @return the enablePagination
     */
    public boolean isEnablePagination() {
        return enablePagination;
    }

    /**
     * @param enablePagination the enablePagination to set
     */
    public void setEnablePagination(boolean enablePagination) {
        this.enablePagination = enablePagination;
    }

    /**
     * @return the paginationText
     */
    public String getPaginationText() {
        return paginationText;
    }

    /**
     * @param paginationText the paginationText to set
     */
    public void setPaginationText(String paginationText) {
        this.paginationText = paginationText;
    }
    
}
