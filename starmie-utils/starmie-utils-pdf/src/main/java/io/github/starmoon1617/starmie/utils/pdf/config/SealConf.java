/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.pdf.config;

import io.github.starmoon1617.starmie.utils.pdf.enums.SealType;
import io.github.starmoon1617.starmie.utils.pdf.image.SealImage;

/**
 * Seal configuration
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class SealConf {

    /**
     * Seal Type
     */
    private SealType type;

    /**
     * Seal image
     */
    private SealImage image;

    /**
     * 签章位置 X, position X
     */
    private float posX;

    /**
     * 签章位置 Y, position Y
     */
    private float posY;

    /**
     * @param type
     * @param image
     * @param posX
     * @param posY
     */
    public SealConf(SealType type, SealImage image, float posX, float posY) {
        super();
        this.type = type;
        this.image = image;
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * @return the type
     */
    public SealType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(SealType type) {
        this.type = type;
    }

    /**
     * @return the image
     */
    public SealImage getImage() {
        return image;
    }

    /**
     * @param image
     *            the image to set
     */
    public void setImage(SealImage image) {
        this.image = image;
    }

    /**
     * @return the posX
     */
    public float getPosX() {
        return posX;
    }

    /**
     * @param posX
     *            the posX to set
     */
    public void setPosX(float posX) {
        this.posX = posX;
    }

    /**
     * @return the posY
     */
    public float getPosY() {
        return posY;
    }

    /**
     * @param posY
     *            the posY to set
     */
    public void setPosY(float posY) {
        this.posY = posY;
    }

}
