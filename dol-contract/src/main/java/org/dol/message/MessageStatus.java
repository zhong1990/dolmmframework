package org.dol.message;

public enum MessageStatus implements GetMessageStatus {
    OK(10000, "成功"),
    SYS_ERROR(10001, "系统繁忙,请稍后重试"),
    SAME_DATA_EXISTS(10002, "%s"),
    DATA_NOT_EXISTS(10003, "%s"),
    DATA_IS_DELETED(10004, "%s已经被删除"),
    DATA_INVALID_DICT_ITEM(10005, "%s不是合法的%s值"),
    PARAM_NOT_VALID(10006, "参数不合法"),
    PARAM_NOT_CORRECT(10007, "必要的参数没有全部正确的提供"),
    PARAM_NOT_SUPPORT(10009, "参数%s没有提供"),

    // common message
    FIELD_NOT_ALLOWED_EMPTY(10010, "%s不能为空"),
    FIELD_VALUE_MUST_LARGE_THAN(10011, "%s必须大于%s"),
    FIELD_VALUE_MUST_LESS_THAN(10012, "%s必须小于%s"),
    FIELD_NOT_EMAIL(10013, "%s不是邮箱格式"),
    FIELD_NOT_MOBILE(10014, "%s不是手机号码"),
    FIELD_NOT_ID_NUM(10015, "%s不是身份证"),
    FIELD_NOT_DATE(10016, "%s不是日期格式"),
    FIELD_NOT_NUM(10016, "%s不是数字"),
    FIELD_LENGTH_MUST_LESS(10017, "%s必须小于%d"),
    FIELD_LENGTH_MUST_MORE(10018, "%s必须大于%d"),
    FIELD_LENGTH_MUST_BETWEEN(10019, "%s长度必须大于%d和小于%d"),
    FILED_NOT_IN_ENUM_VALUES(10020, "不支持的%s"),
    FIELD_NOT_BOTH_BE_EMPTY(10021, "%s不能同时为空"),
    CONTAINS_ILLEGAL_CHARACTERS(10021, "%s含有非法字符"),

    // api invoke error
    API_INVLID_SIGN(10100, "签名错误"),
    API_INVLID_FROM(10101, "没有权限访问"),
    API_INVLID_DATA(10102, "请求数据错误"),
    API_NOT_SUPPORT_METHOD(10103, "只接受Post请求"),
    API_INVLID_DATA_FORMAT(10104, "数据格式不符合要求"),
    API_INVLID_API(10105, "api【%s】不存在"),;

    private int status;
    private String message;

    MessageStatus(int status, String message) {
        setStatus(status);
        setMessage(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getStatus() {
        return status;
    }

    private void setStatus(int status) {
        this.status = status;
    }
}
