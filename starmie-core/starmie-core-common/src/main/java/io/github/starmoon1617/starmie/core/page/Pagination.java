/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.core.page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页, 页码从0开始 Pagination, pageNo start with 0
 * 
 * @date 2023-10-09
 * @author Nathan Liao
 */
public class Pagination<T> implements Serializable {

    private static final long serialVersionUID = 6720759642759607955L;

    /**
     * total count
     */
    private int total;

    /**
     * total page count
     */
    private int totalPage;

    /**
     * size per page
     */
    private int pageSize;

    /**
     * current page No, start with 0
     */
    private int pageNo;

    /**
     * size of elements
     */
    private int size;

    /**
     * List of elements
     */
    private List<T> elms;

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total
     *            the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return the totalPage
     */
    public int getTotalPage() {
        return totalPage;
    }

    /**
     * @param totalPage
     *            the totalPage to set
     */
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize
     *            the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the pageNo
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * @param pageNo
     *            the pageNo to set
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size
     *            the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the elms
     */
    public List<T> getElms() {
        return elms;
    }

    /**
     * @param elms
     *            the elms to set
     */
    public void setElms(List<T> elms) {
        this.elms = elms;
    }

}
