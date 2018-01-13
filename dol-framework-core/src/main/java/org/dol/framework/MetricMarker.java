package org.dol.framework;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MetricMarker {
    private long start;
    private long end;
    private String name;

    MetricMarker(String name) {
        this.start = System.currentTimeMillis();
        this.name = name;
        System.out.println(getTimestamp(start) + ":" + name + "--开始");
    }

    public static MetricMarker start(String name) {
        return new MetricMarker(name);
    }

    private String getTimestamp(long start2) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
        return sdf.format(new Date(start2));
    }

    public void end() {
        end = System.currentTimeMillis();
        System.out.println(getTimestamp(end) + ":" + name + "--结束，用时：" + getUseTime() + "毫秒");
    }

    private long getUseTime() {
        return end - start;
    }

}
