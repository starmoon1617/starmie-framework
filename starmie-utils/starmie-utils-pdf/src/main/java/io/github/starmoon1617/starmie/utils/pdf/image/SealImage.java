/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.pdf.image;

import java.io.Serializable;

/**
 * Seal Image
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class SealImage implements Serializable {

    private static final long serialVersionUID = -108077271077290218L;
    
    /**
     * width
     */
    private int width;

    /**
     * height
     */
    private int height;

    /**
     * ext name
     */
    private String ext;

    /**
     * image content
     */
    private byte[] content;
    
    /**
     * @param width
     * @param height
     * @param ext
     * @param content
     */
    public SealImage(int width, int height, String ext, byte[] content) {
        super();
        this.width = width;
        this.height = height;
        this.ext = ext;
        this.content = content;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width
     *            the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the ext
     */
    public String getExt() {
        return ext;
    }

    /**
     * @param ext
     *            the ext to set
     */
    public void setExt(String ext) {
        this.ext = ext;
    }

    /**
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

}
