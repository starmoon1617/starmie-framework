/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.pdf.write;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.starmoon1617.starmie.utils.doc.head.DocHead;
import io.github.starmoon1617.starmie.utils.pdf.config.PageConf;
import io.github.starmoon1617.starmie.utils.pdf.config.SealConf;
import io.github.starmoon1617.starmie.utils.pdf.font.PdfFonts;

/**
 * Utility Class for writing PDF
 * PDF 写工具类
 * 
 * @date 2023-10-10
 * @author Nathan Liao
 */
public class PdfWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfWriter.class);

    private PdfWriter() {

    }

    /**
     * write data list to PDF 直接将数据写入到一个PDF
     * 
     * @param <E>
     * @param os  - PDF output stream
     * @param sheetName 
     * @param heads
     * @param datas - data to write
     * @param headFontIn - head font input stream
     * @param textFontIn - text font input stream
     * @param sealConf - Seal
     */
    public static <E> void WriteToPdf(OutputStream os, String sheetName, List<DocHead> heads, List<E> datas, InputStream headFontIn, InputStream textFontIn,
            SealConf sealConf) {
        PDDocument pdDocument = null;
        PdfWriteHandler<E> pdfWriteHandler = null;
        try {
            pdDocument = new PDDocument();
            PageConf pageConf = new PageConf();
            pageConf.setFonts(new PdfFonts(PDType0Font.load(pdDocument, headFontIn, true), PDType0Font.load(pdDocument, textFontIn, true)));
            pdfWriteHandler = new PdfWriteHandler<>(pdDocument, sheetName, heads, pageConf, sealConf);
            pdfWriteHandler.writeDatas(datas);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pdfWriteHandler != null) {
                    pdfWriteHandler.close();
                }
                if (pdDocument != null) {
                    pdDocument.save(os);
                    pdDocument.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                LOGGER.error("inputstream or pdDocument close exception: {}", e.getMessage());
            }
        }
    }

    /**
     * 创建一个 PDF Write Handler, build PDF Write Handler
     * 
     * @param <E>
     * @param sheetName
     * @param heads
     * @param headFontIn
     * @param textFontIn
     * @param sealConf
     * @return
     */
    public static <E> PdfWriteHandler<E> buildPdfWriteHandler(String sheetName, List<DocHead> heads, InputStream headFontIn, InputStream textFontIn,
            SealConf sealConf) {
        try {
            PDDocument pdDocument = new PDDocument();
            PageConf pageConf = new PageConf();
            pageConf.setFonts(new PdfFonts(PDType0Font.load(pdDocument, headFontIn, true), PDType0Font.load(pdDocument, textFontIn, true)));
            return new PdfWriteHandler<>(pdDocument, sheetName, heads, pageConf, sealConf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用handler写入数据
     * 
     * @param <E>
     * @param handler
     * @param datas
     */
    public static <E> void writeDatas(PdfWriteHandler<E> handler, List<E> datas) {
        if (handler != null) {
            handler.writeDatas(datas);
        }
    }

    /**
     * 将Excel数据写入输出流
     * 
     * @param <E>
     * @param os
     * @param handler
     */
    public static <E> void flush(OutputStream os, PdfWriteHandler<E> handler) {
        try {
            if (handler == null) {
                return;
            }
            handler.close();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (handler != null) {
                    handler.getPDDocument().save(os);
                    handler.getPDDocument().close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                LOGGER.error("inputstream or pdDocument close exception: {}", e.getMessage());
            }
        }
    }

}
