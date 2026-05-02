/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.app.web;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.github.starmoon1617.starmie.core.app.constant.Constants;
import io.github.starmoon1617.starmie.core.app.enums.MimeType;
import io.github.starmoon1617.starmie.core.base.BaseDto;
import io.github.starmoon1617.starmie.core.base.BaseEntity;
import io.github.starmoon1617.starmie.core.constant.InterpunctionConstants;
import io.github.starmoon1617.starmie.core.criterion.BaseCriteria;
import io.github.starmoon1617.starmie.core.criterion.enums.LimitationType;
import io.github.starmoon1617.starmie.core.util.CommonUtils;
import io.github.starmoon1617.starmie.core.util.EntityUtils;
import io.github.starmoon1617.starmie.utils.doc.enums.DateMode;
import io.github.starmoon1617.starmie.utils.doc.head.DocHead;
import io.github.starmoon1617.starmie.utils.poi.read.ExcelReadHandler;
import io.github.starmoon1617.starmie.utils.poi.read.ExcelReader;
import io.github.starmoon1617.starmie.utils.poi.read.RowReadListener;
import io.github.starmoon1617.starmie.utils.poi.write.ExcelWriteHandler;
import io.github.starmoon1617.starmie.utils.poi.write.ExcelWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Abstract Controller for Entity Provides page navigation methods and
 * file-based import/export
 * 
 * @date 2023-10-23
 * @author Nathan Liao
 */
public abstract class BaseWebController<E extends BaseEntity<ID, U>, ID extends Serializable, U extends Serializable>
        extends BaseGenericController<E, ID, U> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseWebController.class);

    /**
     * return base path for view
     * 
     * @return
     */
    protected abstract String getViewBasePath();

    /**
     * Go to list page
     * 
     * @return
     */
    @RequestMapping(value = "/toList", method = RequestMethod.GET)
    public String toList() {
        StringBuilder sb = new StringBuilder();
        sb.append(getViewBasePath()).append(InterpunctionConstants.SLASH).append("list");
        return sb.toString();
    }

    /**
     * Go to add page
     * 
     * @return
     */
    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public String toAdd() {
        StringBuilder sb = new StringBuilder();
        sb.append(getViewBasePath()).append(InterpunctionConstants.SLASH).append("add");
        return sb.toString();
    }

    /**
     * Go to edit page
     * 
     * @param e
     * @param model
     * @return
     */
    @RequestMapping(value = "/toEdit", method = RequestMethod.GET)
    public String toEdit(E e, Model model) {
        model.addAttribute("entity", getManager().find(e));
        StringBuilder sb = new StringBuilder();
        sb.append(getViewBasePath()).append(InterpunctionConstants.SLASH).append("edit");
        return sb.toString();
    }

    /**
     * Go to delete page
     * 
     * @param e
     * @param model
     * @return
     */
    @RequestMapping(value = "/toDelete", method = RequestMethod.GET)
    public String toDelete(E e, Model model) {
        model.addAttribute("entity", getManager().find(e));
        StringBuilder sb = new StringBuilder();
        sb.append(getViewBasePath()).append(InterpunctionConstants.SLASH).append("delete");
        return sb.toString();
    }

    /**
     * Set Content type for file export
     * 
     * @param fileName
     * @param response
     * @param mimeType
     * @param fileExt
     * @throws Exception
     */
    protected void setFileContentType(String fileName, HttpServletResponse response, String mimeType, String fileExt)
            throws Exception {
        response.setContentType(mimeType);
        response.setCharacterEncoding(Constants.UTF_8);
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, Constants.UTF_8) + fileExt);
        response.setHeader("Pragma", "no-cache");
    }

    /**
     * add converters for export heads
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
     * get Date mode for excel export, default Date time mode
     * 
     * @return
     */
    protected DateMode getDateMode() {
        return DateMode.DATETIME;
    }

    /**
     * Excel file export
     * 
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public void doExport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = request.getParameter("fileName");
        if (!CommonUtils.isNotBlank(fileName)) {
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

            handler = ExcelWriter.buildExcelWriteHandler(fileName, getExportHeads(request), getDateMode());

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
            if (handler != null) {
                ExcelWriter.flush(response.getOutputStream(), handler);
            }
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
     * Excel file import
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public BaseDto<String> doImport(HttpServletRequest request) {
        String errorMsg = null;
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("uploadFile");
        if (file == null || file.isEmpty()) {
            return getFailure(-1, "Upload file is empty!");
        }
        try (InputStream is = file.getInputStream()) {
            if (!(request instanceof MultipartHttpServletRequest)) {
                return getFailure(-1, "Request must be multipart!");
            }

            List<E> datas = ExcelReader.read(is, getImportHeads(request), getExcelReadHandler(), getEntityType(),
                    getRowReadListener());
            if (CommonUtils.isEmpty(datas)) {
                return getSuccess("Success! No data to import.");
            }
            errorMsg = validateImportDatas(datas);
            if (!CommonUtils.isNotBlank(errorMsg)) {
                batchSave(datas);
            }
        } catch (Exception e) {
            LOGGER.error("Import Error : ", e);
            errorMsg = String.format("Import Error: %s", e.getMessage());
        }
        if (CommonUtils.isNotBlank(errorMsg)) {
            return getFailure(-1, errorMsg);
        }
        return getSuccess("Success!");
    }

}
