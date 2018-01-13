package org.dol.framework.web.common;

import org.dol.framework.util.DateUtil;
import org.dol.framework.util.StringUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dolphin on 2017/7/21.
 */
public class SessionManager {


    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String name, Class<T> clazz) {
        Object object = get(name);
        return object == null ? null : (List<T>) object;
    }

    public void set(String name, Object value) {
        RequestContext.currentRequest().getSession().setAttribute(name, value);
    }

    public Object get(String name) {
        return RequestContext.currentRequest().getSession().getAttribute(name);
    }

    public String getString(String name) {
        Object value = RequestContext.currentRequest().getSession().getAttribute(name);
        return value == null ? null : value.toString();
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String name, Class<T> clazz) {
        Object object = get(name);
        return object == null ? null : (T) object;
    }

    public Integer getInteger(String name) {
        String str = getString(name);
        return StringUtil.isBlank(str) ? null : Integer.valueOf(str);
    }

    public Long getLong(String name) {
        String str = getString(name);
        return StringUtil.isBlank(str) ? null : Long.valueOf(str);
    }

    public Double getDouble(String name) {
        String str = getString(name);
        return StringUtil.isBlank(str) ? null : Double.valueOf(str);
    }

    public Byte getByte(String name) {
        String str = getString(name);
        return StringUtil.isBlank(str) ? null : Byte.valueOf(str);
    }

    public Boolean getBoolean(String name) {
        String str = getString(name);
        return StringUtil.isBlank(str) ? null : Boolean.valueOf(str);
    }

    public BigDecimal getBigDecimal(String name) {
        String str = getString(name);
        return StringUtil.isBlank(str) ? null : new BigDecimal(str);
    }

    public Float getFloat(String name) {
        String str = getString(name);
        return StringUtil.isBlank(str) ? null : Float.valueOf(str);
    }

    public Date getDate(String name) {
        String str = getString(name);
        if (StringUtil.isBlank(str)) {
            return null;
        }
        if (StringUtil.isNumeric(str)) {
            long times = Long.parseLong(str);
            return new Date(times);
        }
        return parse(str);
    }

    private Date parse(String value) {
        try {
            return DateUtil.getDate(value, DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("日期格式不支持，无法转成日期：" + value);
        }
    }

    public Calendar getCalendar(String name) {
        Date date = getDate(name);
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
