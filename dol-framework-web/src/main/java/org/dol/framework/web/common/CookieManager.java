package org.dol.framework.web.common;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Attention: don't use urlEncode to encode the cookie value,
 * because this tool will encode the cookie value
 */
public abstract class CookieManager {

    /**
     * 返回cookie，如果不存在，返回null
     *
     * @param name
     * @return
     */
    public static Cookie get(String name) {
        Cookie[] cookies = RequestContext.currentRequest().getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * 返回所有cookie
     *
     * @return
     */
    public static Cookie[] getAll() {
        Cookie[] cookies = HttpServletUtil.currentRequest().getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.copyOf(cookies, cookies.length);
    }

    /**
     * 清空所有cookie
     *
     * @param name
     */
    public static void clear(String name) {
        Cookie[] cookies = HttpServletUtil.currentRequest().getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                del(cookie.getName());
            }
        }
    }


    /**
     * 返回cookie的值，如果不存在，返回null
     *
     * @param name
     * @return
     */
    public static String getValue(String name) {
        Cookie cookie = get(name);
        return cookie == null ? null : decode(cookie.getValue());
    }


    /**
     * 删除Cookie
     *
     * @param name
     */
    public static void del(String name) {
        Cookie cookie = get(name);
        if (cookie == null) {
            cookie = new Cookie(name, "");
        }
        cookie.setMaxAge(-1);
        add(cookie);
    }

    /**
     * 添加Cookie
     *
     * @param cookie
     */
    public static void add(Cookie cookie) {
        if (cookie.getValue() != null) {
            String encodeCookieValue = encode(cookie.getValue());
            cookie.setValue(encodeCookieValue);
        }
        HttpServletUtil.currentResponse().addCookie(cookie);
    }

    private static String encode(String val) {
        try {
            return URLEncoder.encode(val, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            //never happened
            return null;
            //  e.printStackTrace();
        }
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            //never happened
            return null;
        }
    }

    /**
     * 添加Cookie
     *
     * @param name
     * @param value
     */
    public static void add(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        add(cookie);
    }

    /**
     * 添加Cookie
     *
     * @param name
     * @param value
     * @param domain
     * @param path
     * @param secure
     * @param expiry
     */
    public static void add(String name, String value, String domain, String path, Boolean secure, Integer expiry) {
        Cookie cookie = new Cookie(name, value);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        if (path != null) {
            cookie.setPath(path);
        }
        if (expiry != null) {
            cookie.setMaxAge(expiry);
        }
        if (secure != null) {
            cookie.setSecure(secure);
        }
        add(cookie);
    }

}
