package org.dol.framework.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.dol.framework.web.common.SessionManager;
import org.dol.framework.web.model.BSQueryResult;
import org.dol.message.MessageInfo;
import org.dol.message.QueryResultInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * 基础ACTION,获得writeJson和ActionSupport的功能.
 */
public class BaseController {

    @Autowired
    protected SessionManager sessionManager;

    public <T> BSQueryResult<T> toBSQueryResult(QueryResultInfo<T> data) {
        return BSQueryResult.from(data);
    }

    public <T> String toBSQueryResultString(QueryResultInfo<T> data) {
        BSQueryResult<T> bsQueryResult = BSQueryResult.from(data);
        return JSON.toJSONString(bsQueryResult,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullListAsEmpty);
    }

    public <T> String toBSQueryResultString(MessageInfo<QueryResultInfo<T>> queryResultInfoMessageInfo) {
        BSQueryResult<T> bsQueryResult = null;
        if (queryResultInfoMessageInfo.success()) {
            bsQueryResult = BSQueryResult.from(queryResultInfoMessageInfo.getData());
        } else {
            bsQueryResult = new BSQueryResult<>();
            bsQueryResult.setRecordsTotal(0L);
            bsQueryResult.setData(new ArrayList<T>(0));
            bsQueryResult.setSuccess(false);
            bsQueryResult.setError(queryResultInfoMessageInfo.getMessage());
        }
        return JSON.toJSONString(bsQueryResult,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullListAsEmpty);
    }
}
