package org.dol.message;

import java.io.Serializable;
import java.util.List;

public class QueryResultInfo<E> implements Serializable {

    private static final long serialVersionUID = -4137328713263172465L;

    private Integer pages;
    private Long total;
    private List<E> records;

    public Integer getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages.intValue();
    }

    public List<E> getRecords() {
        return records;
    }

    public void setRecords(List<E> records) {
        this.records = records;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public void setTotal(Integer total) {
        this.total = total.longValue();
    }

}
