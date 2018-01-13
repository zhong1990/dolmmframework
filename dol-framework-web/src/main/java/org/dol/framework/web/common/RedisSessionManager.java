package org.dol.framework.web.common;

import com.alibaba.fastjson.JSON;
import org.dol.framework.util.StringUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dolphin on 2017/8/23.
 */
public class RedisSessionManager extends SessionManager {

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString("1"));
        System.out.println(JSON.toJSONString(Long.valueOf("1")));
        System.out.println(JSON.toJSONString(new BigDecimal("1")));
        System.out.println(JSON.toJSONString(Boolean.valueOf("1")));
        System.out.println(JSON.toJSONString(Byte.valueOf("1")));
        System.out.println(JSON.toJSONString(Float.valueOf("1")));
        System.out.println(JSON.toJSONString(Short.valueOf("1")));

    }

    public <T> List<T> getList(String name, Class<T> clazz) {
        String str = getString(name);
        return StringUtil.isBlank(str) ? null : JSON.parseArray(str, clazz);
    }

    @Override
    public void set(String name, Object o) {
        if (o == null) {
            RequestContext.currentRequest().getSession().removeAttribute(name);
        }
        if (o instanceof String) {
            super.set(name, o);
            return;
        }
        if (o instanceof Number
                || o instanceof Boolean
                || o instanceof Character) {
            super.set(name, o.toString());
            return;
        }

        if (o instanceof Date) {
            Date date = (Date) o;
            long times = date.getTime();
            super.set(name, String.valueOf(times));
            return;
        }
        if (o instanceof Calendar) {
            Calendar date = (Calendar) o;
            long times = date.getTimeInMillis();
            super.set(name, String.valueOf(times));
            return;
        }
        super.set(name, JSON.toJSONString(o));
    }

    @Override
    public <T> T getObject(String name, Class<T> clazz) {
        String string = getString(name);
        return StringUtil.isBlank(string) ? null : JSON.parseObject(string, clazz);
    }
}
