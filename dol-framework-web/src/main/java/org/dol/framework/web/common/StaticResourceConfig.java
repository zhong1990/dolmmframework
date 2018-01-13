/**
 *
 */
package org.dol.framework.web.common;

import org.dol.framework.util.ListUtil;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取上传文件表单信息.
 *
 * @author dolphin
 * @date 2017年4月5日 上午10:14:39
 */
public class StaticResourceConfig implements InitializingBean {

    public static final String JS_TEMPLATE = "<script src=\"#src#\" type=\"text/javascript\" charset=\"UTF-8\"></script>\n";
    public static final String CSS_TEMPLATE = "<link rel=\"stylesheet\" href=\"#href#\">\n";
    private volatile boolean isInit;
    private List<StaticResourcePlugin> plugins;
    private Map<String, StaticResourcePlugin> pluginMap;

    public String renderCss(String... names) {
        makeSureInit();
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            StaticResourcePlugin plugin = pluginMap.get(name);
            if (plugin != null) {
                for (String url : plugin.getCssList()) {
                    sb.append(CSS_TEMPLATE.replace("#href#", url));
                }
            }
        }
        return sb.toString();
    }

    public String renderJS(String... names) {
        makeSureInit();
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            StaticResourcePlugin plugin = pluginMap.get(name);
            if (plugin != null) {
                for (String url : plugin.getJsList()) {
                    sb.append(JS_TEMPLATE.replace("#src#", url));
                }
            }
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    private void makeSureInit() {
        if (isInit) {
            return;
        }
        synchronized (this) {
            if (isInit) {
                return;
            }
            init();
            isInit = true;
        }
    }

    /**
     * 参照方法名.
     */
    private void init() {
        pluginMap = new HashMap<>(this.plugins.size());
        for (StaticResourcePlugin plugin : this.plugins) {
            pluginMap.put(plugin.getName(), plugin);
            List<String> newCssList = getFullUrlList(plugin.getCssList(), plugin.getVersion());
            plugin.setCssList(newCssList);
            List<String> newJsList = getFullUrlList(plugin.getJsList(), plugin.getVersion());
            plugin.setJsList(newJsList);
        }
    }

    private List<String> getFullUrlList(List<String> oldList, String version) {
        if (ListUtil.isNullOrEmpty(oldList)) {
            return new ArrayList<String>(0);
        }
        List<String> list = new ArrayList<String>(oldList.size());
        for (String url : oldList) {
            if (url.startsWith("/")) {
                list.add(HttpServletUtil.getContextPath() + url + "?=" + version);
            } else {
                list.add(HttpServletUtil.getContextPath() + "/" + url + "?=" + version);
            }
        }
        return list;
    }

    public List<StaticResourcePlugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<StaticResourcePlugin> plugins) {
        this.plugins = plugins;
    }
}
