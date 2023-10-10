/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.utils.poi.read;

import org.apache.poi.ooxml.extractor.POIXMLExtractorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

}
