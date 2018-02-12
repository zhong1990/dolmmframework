package org.dol.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryBaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int pageIndex = 1;
    private int pageSize = 10;
    /**
     * 是否分页
     */
    private boolean paging = true;
    private String order;
    private String orderBy;
    private String returnFields;

    private List<SortField> sortFields = new ArrayList<SortField>();

    public void addFirstSortField(SortField sortField) {
        getSortFields().add(0, sortField);
    }

    public void addSortField(int index, SortField sortField) {
        getSortFields().add(index, sortField);
    }

    public void addSortField(String name, boolean direction) {
        getSortFields().add(new SortField(name, direction));
    }

    public void appendSortField(SortField sortField) {
        getSortFields().add(sortField);
    }

    public int getEndIndex() {
        return pageIndex * pageSize;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getReturnFields() {
        return returnFields;
    }

    public void setReturnFields(String returnFields) {
        this.returnFields = returnFields;
    }

    public List<SortField> getSortFields() {
        return sortFields != null ? sortFields : new ArrayList<SortField>();
    }

    public void setSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
    }

    public int getStartIndex() {
        return (pageIndex - 1) * pageSize;
    }

    public boolean getPaging() {
        return paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }
}
