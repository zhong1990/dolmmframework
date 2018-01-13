package org.dol.framework.web.common;

import java.util.List;

/**
 * Created by dolphin on 2017/7/20.
 */
public class StaticResourcePlugin {

    private String name;
    private String version;
    private List<String> jsList;
    private List<String> cssList;

    public List<String> getCssList() {
        return cssList;
    }

    public void setCssList(List<String> cssList) {
        this.cssList = cssList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getJsList() {
        return jsList;
    }

    public void setJsList(List<String> jsList) {
        this.jsList = jsList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
