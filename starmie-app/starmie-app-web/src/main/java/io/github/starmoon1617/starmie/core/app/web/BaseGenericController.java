/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.app.web;

import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.github.starmoon1617.starmie.core.app.base.BaseController;
import io.github.starmoon1617.starmie.core.app.constant.Constants;
import io.github.starmoon1617.starmie.core.app.enums.MimeType;
import io.github.starmoon1617.starmie.core.base.BaseDto;
import io.github.starmoon1617.starmie.core.constant.InterpunctionConstants;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;
import io.github.starmoon1617.starmie.core.criterion.enums.LimitationType;
import io.github.starmoon1617.starmie.core.util.CommonUtils;
import io.github.starmoon1617.starmie.core.util.EntityUtils;
import io.github.starmoon1617.starmie.utils.doc.head.DocHead;
import io.github.starmoon1617.starmie.utils.poi.read.ExcelReadHandler;
import io.github.starmoon1617.starmie.utils.poi.read.ExcelReader;
import io.github.starmoon1617.starmie.utils.poi.read.RowReadListener;
import io.github.starmoon1617.starmie.utils.poi.write.ExcelWriteHandler;
import io.github.starmoon1617.starmie.utils.poi.write.ExcelWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Generic Controller
 * 
 * @date 2023-10-23
 * @author Nathan Liao
 */
public class BaseGenericController<E> extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseGenericController.class);

    /**
     * 设置导出文件的Content type
     * 
     * @param fileName
     * @param response
     * @throws Exception
     */
    protected void setFileContentType(String fileName, HttpServletResponse response, String mimeType, String fileExt) throws Exception {
        response.setContentType(mimeType);
        response.setCharacterEncoding(Constants.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, Constants.UTF_8) + fileExt);
        response.setHeader("Pragma", "no-cache");
    }

    /**
     * return count for export
     * 
     * @return
     */
    protected int count(BaseCriteria criteria) {
        return 0;
    }

    /**
     * return list for export
     * 
     * @param criteria
     * @return
     */
    protected List<E> find(BaseCriteria criteria) {
        return null;
    }

    /**
     * save import data
     */
    protected void batchSave(List<E> datas) {

    }

    /**
     * add converters
     * 
     * @param heads
     */
    protected void addConverters(List<DocHead> heads) {

    }

    /**
     * get export head from request
     * 
     * @param request
     * @return
     * @throws Exception
     */
    protected List<DocHead> getExportHeads(HttpServletRequest request) throws Exception {
        List<DocHead> docHeads = EntityUtils.fromJsonToList(request.getParameter("heads"), DocHead.class);
        addConverters(docHeads);
        return docHeads;
    }

    /**
     * export
     * 
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public void doExport(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String fileName = request.getParameter("fileName");
        if (CommonUtils.isNotBlank(fileName)) {
            fileName = "export_datas";
        }

        setFileContentType(fileName, response, MimeType.XLSX.getType(), MimeType.XLSX.getExt());
        ExcelWriteHandler<E> handler = null;
        try {
            BaseCriteria baseCriteria = getCriteria(request);
            if (baseCriteria.getLimit() == null) {
                baseCriteria.addLimitation(LimitationType.LIMIT, Constants.EXPORT_PAGE_SIZE);
            }
            int pageSize = baseCriteria.getLimit();

            handler = ExcelWriter.buildExcelWriteHandler(fileName, getExportHeads(request));

            int total = count(baseCriteria);
            if (total <= 0) {
                ExcelWriter.writeDatas(handler, new ArrayList<E>(1));
                return;
            }

            int cnt = 0;
            int totalCnt = 0;
            for (int i = 0;; i++) {
                baseCriteria.addLimitation(LimitationType.OFFSET, pageSize * i);
                List<E> datas = find(baseCriteria);
                if (CommonUtils.isEmpty(datas)) {
                    break;
                }
                ExcelWriter.writeDatas(handler, datas);
                cnt = datas.size();
                totalCnt = totalCnt + cnt;
                if (cnt < pageSize || totalCnt >= total) {
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Export Exception:", e);
            throw e;
        } finally {
            ExcelWriter.flush(response.getOutputStream(), handler);
        }
    }

    /**
     * return Excel read handler
     * 
     * @return
     */
    protected ExcelReadHandler<E> getExcelReadHandler() {
        return null;
    }

    /**
     * return row listener
     * 
     * @return
     */
    protected RowReadListener<E> getRowReadListener() {
        return null;
    }

    /**
     * return import heads
     * 
     * @param request
     * @return
     */
    protected List<String> getImportHeads(HttpServletRequest request) {
        return CommonUtils.splitToList(request.getParameter("heads"), InterpunctionConstants.COMMA_STR);
    }

    /**
     * validate datas
     * 
     * @param datas
     * @return
     */
    protected String validateImportDatas(List<E> datas) {
        return null;
    }

    /**
     * data import
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/import")
    public BaseDto<String> doImport(HttpServletRequest request) {
        String errorMsg = null;
        try {
            MultipartFile file = MultipartHttpServletRequest.class.cast(request).getFile("uploadFile");
            List<E> datas = ExcelReader.read(file.getInputStream(), getImportHeads(request), getExcelReadHandler(),
                    ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0], getRowReadListener());
            if (CommonUtils.isEmpty(datas)) {
                return getSuccess("Success!");
            }
            errorMsg = validateImportDatas(datas);
            if (!CommonUtils.isNotBlank(errorMsg)) {
                batchSave(datas);
            }
        } catch (Exception e) {
            LOGGER.error("import Error : ", e);
            errorMsg = String.format("import Error:%s", e.getMessage());
        }
        if (CommonUtils.isNotBlank(errorMsg)) {
            return getFailure(-1, errorMsg);
        }
        return getSuccess("Success!");
    }
}
