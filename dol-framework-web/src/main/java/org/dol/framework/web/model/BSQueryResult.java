/**
 * studio
 * QueryResult.java
 * org.dol.studio.model
 * TODO
 *
 * @author dolphin
 * @date 2016年6月12日 下午1:34:56
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.web.model;

import org.dol.message.QueryResultInfo;

import java.util.List;

/**
 * 通用查询结果对象.
 *
 * @param <E> 返回数据类型
 * @author dolphin
 * @Create 2017年2月13日 下午6:11:27
 * @since 1.7
 */
public class BSQueryResult<E> {

    private Integer draw;
    private Long recordsTotal;
    private Long recordsFiltered;
    private List<E> data;
    private String error;
    private boolean success = true;


    public static <E> BSQueryResult<E> from(QueryResultInfo<E> queryResultInfo) {
        BSQueryResult<E> queryResult = new BSQueryResult<E>();
        queryResult.setData(queryResultInfo.getRecords());
        queryResult.setRecordsTotal(queryResultInfo.getTotal());
        return queryResult;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Long recordsTotal) {
        this.recordsFiltered = this.recordsTotal = recordsTotal;
    }

    public Long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
