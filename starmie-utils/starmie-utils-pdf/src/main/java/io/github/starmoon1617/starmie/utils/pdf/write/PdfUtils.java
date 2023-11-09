/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.pdf.write;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import io.github.starmoon1617.starmie.utils.pdf.config.PageConf;
import io.github.starmoon1617.starmie.utils.pdf.config.SealConf;
import io.github.starmoon1617.starmie.utils.pdf.enums.PageOrientation;
import io.github.starmoon1617.starmie.utils.pdf.image.SealImage;

/**
 * Utility Class for PDF
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class PdfUtils {
    
    /**
     * 1英寸对应的毫米长度(25.4)
     */
    private static final float INCH_LENGTH = 25.4F;

    private PdfUtils() {

    }

    /**
     * 根据纸张的大小类型,纵横类型和 DPI 计算出纸张的 像素大小
     * calculate page Rectangle according to page type
     * 
     * @param pageConf
     * @return
     */
    public static PDRectangle getPageRectangle(PageConf pageConf) {
        if (pageConf.getPdRectangle() != null) {
            return pageConf.getPdRectangle();
        }

        float width = 0f;
        float height = 0f;

        if (pageConf.getOrientation() == PageOrientation.PORTRAIT) {
            width = pageConf.getPageType().getWidth();
            height = pageConf.getPageType().getHeight();
        } else {
            width = pageConf.getPageType().getHeight();
            height = pageConf.getPageType().getWidth();
        }
        // calculate PX
        width = width * pageConf.getPpi() / INCH_LENGTH;
        height = height * pageConf.getPpi() / INCH_LENGTH;

        // set page Rectangle to page configuration
        pageConf.setPageWidth(width);
        pageConf.setPageHeight(height);
        pageConf.setPdRectangle(new PDRectangle(width, height));

        return pageConf.getPdRectangle();
    }

    /**
     * 调整修正签章的位置
     * adjust position for seal
     * 
     * @param sealConf
     * @param posX
     * @param posY
     * @param pageWidth
     * @param pageHeight
     */
    public static void adjustPos(SealConf sealConf, float posX, float posY, float pageWidth, float pageHeight) {
        sealConf.setPosX(posX);
        if (posX + sealConf.getImage().getWidth() > pageWidth) {
            sealConf.setPosX(pageWidth - sealConf.getImage().getWidth());
        }
        sealConf.setPosY(posY);
        if (posY + sealConf.getImage().getHeight() > pageHeight) {
            sealConf.setPosY(pageHeight - sealConf.getImage().getHeight());
        }
    }

    /**
     * SealConf.CROSS_PAGE 调整多页
     * adjust for cross page seal
     * 
     * @param sealConf
     * @param totalPage
     * @param posX
     * @param posY
     * @param pageWidth
     * @param pageHeight
     * @return
     */
    public static List<SealConf> adjustCrossPage(SealConf sealConf, int totalPage, float posX, float posY, float pageWidth, float pageHeight) throws Exception {
        List<SealConf> list = new ArrayList<>();
        SealImage image = sealConf.getImage();
        // remainder for width
        int mw = image.getWidth() % totalPage;
        // width per page 
        int pw = (image.getWidth() - mw) / totalPage;
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(image.getExt());
        ImageReader ir = it.next();
        ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(image.getContent()));
        ir.setInput(iis, true);
        int startX = 0;
        int startY = 0;
        int index = 0;
        for (int i = 0; i < totalPage; i++) {
            int nw = ((i < mw) ? pw + 1 : pw);
            ImageReadParam irp = ir.getDefaultReadParam();
            irp.setSourceRegion(new Rectangle(startX, startY, nw, image.getHeight()));
            BufferedImage bi = ir.read(index, irp);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, image.getExt(), baos);

            SealImage simg = new SealImage(nw, image.getHeight(), image.getExt(), baos.toByteArray());
            SealConf sc = new SealConf(sealConf.getType(), simg, pageWidth - nw, posY);
            list.add(sc);

            startX = startX + nw;
            index = ir.getMinIndex();
        }
        return list;

    }

    /**
     *  Add Seal image to document
     * @param sealConf
     * @param pdDocument
     * @param currentPdcs
     * @throws Exception
     */
    public static void addSeal(SealConf sealConf, PDDocument pdDocument, PDPageContentStream currentPdcs) throws Exception {
        SealImage image = sealConf.getImage();
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(pdDocument, image.getContent(), image.getExt());
        PDExtendedGraphicsState pdExtGfxState = new PDExtendedGraphicsState();
        // 设置透明度
        pdExtGfxState.setNonStrokingAlphaConstant(0.5f);
        pdExtGfxState.setAlphaSourceFlag(true);
        pdExtGfxState.getCOSObject().setItem(COSName.BM, COSName.MULTIPLY);
        currentPdcs.setGraphicsStateParameters(pdExtGfxState);
        // 1/3文档高度位置
        currentPdcs.drawImage(pdImage, sealConf.getPosX(), sealConf.getPosY(), image.getWidth(), image.getHeight());
    }
    
}
