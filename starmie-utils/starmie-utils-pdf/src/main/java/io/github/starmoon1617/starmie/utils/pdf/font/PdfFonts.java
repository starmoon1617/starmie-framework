/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.pdf.font;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * PDF Fonts
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class PdfFonts {
    
    /**
     * font for head
     */
    private PDFont headFont;

    /**
     * font for text
     */
    private PDFont textFont;

    /**
     * head font size
     */
    private float headFontSize;

    /**
     * test font size
     */
    private float textFontSize;

    /**
     * height of head font
     */
    private float headFontHeight;

    /**
     * height of text font
     */
    private float textFontHeight;

    /**
     * fixed height for head font
     * 修正高度, 当低位为负数时, 低位的比例为修正值
     */
    private float headFixedHeight;

    /**
     * fixed height for text font
     * 修正高度, 当低位为负数时, 低位的比例为修正值
     */
    private float textFixedHeight;

    /**
     * default head font size is 14, default text font size is 12
     * @param headFont
     * @param textFont
     */
    public PdfFonts(PDFont headFont, PDFont textFont) {
        this(headFont, textFont, 14, 12);
    }

    /**
     * @param headFont
     * @param textFont
     * @param headFontSize
     * @param textFontSize
     */
    public PdfFonts(PDFont headFont, PDFont textFont, float headFontSize, float textFontSize) {
        this(headFont, textFont, headFontSize, textFontSize, true);
    }

    /**
     * @param headFont
     * @param textFont
     * @param headFontSize
     * @param textFontSize
     * @param recalc
     *            - false: get fontHeight from BoundingBox's height
     */
    public PdfFonts(PDFont headFont, PDFont textFont, float headFontSize, float textFontSize, boolean recalc) {
        super();
        this.headFont = headFont;
        this.textFont = textFont;
        this.headFontSize = headFontSize;
        this.textFontSize = textFontSize;
        try {
            if (recalc) {
                BoundingBox bb = headFont.getBoundingBox();
                float lowY = bb.getLowerLeftY();
                float highY = bb.getUpperRightY();
                if (highY <= 0) {
                    this.headFontHeight = bb.getHeight();
                    this.headFixedHeight = bb.getHeight();
                } else {
                    this.headFontHeight = highY - Math.max(lowY, 0);
                    this.headFixedHeight = (0 - lowY) / bb.getHeight() * (0 - lowY);
                }

                bb = textFont.getBoundingBox();
                lowY = bb.getLowerLeftY();
                highY = bb.getUpperRightY();
                if (highY <= 0) {
                    this.textFontHeight = bb.getHeight();
                    this.textFixedHeight = bb.getHeight();
                } else {
                    this.textFontHeight = highY - Math.max(lowY, 0);
                    this.textFixedHeight = (0 - lowY) / bb.getHeight() * (0 - lowY);
                }
            } else {
                this.headFontHeight = headFont.getBoundingBox().getHeight();
                this.textFontHeight = textFont.getBoundingBox().getHeight();
                this.headFixedHeight = 0;
                this.textFixedHeight = 0;
            }
            this.headFontHeight = this.headFontHeight / 1000 * headFontSize;
            this.textFontHeight = this.textFontHeight / 1000 * textFontSize;
            this.headFixedHeight = this.headFixedHeight / 1000 * headFontSize;
            this.textFixedHeight = this.textFixedHeight / 1000 * textFontSize;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the headFont
     */
    public PDFont getHeadFont() {
        return headFont;
    }

    /**
     * @return the textFont
     */
    public PDFont getTextFont() {
        return textFont;
    }

    /**
     * @return the headFontSize
     */
    public float getHeadFontSize() {
        return headFontSize;
    }

    /**
     * @return the textFontSize
     */
    public float getTextFontSize() {
        return textFontSize;
    }

    /**
     * @return the headFontHeight
     */
    public float getHeadFontHeight() {
        return headFontHeight;
    }

    /**
     * @return the textFontHeight
     */
    public float getTextFontHeight() {
        return textFontHeight;
    }

    /**
     * @return the headFixedHeight
     */
    public float getHeadFixedHeight() {
        return headFixedHeight;
    }

    /**
     * @return the textFixedHeight
     */
    public float getTextFixedHeight() {
        return textFixedHeight;
    }
    
}
