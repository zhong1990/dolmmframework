package org.dol.framework.queue;

import java.util.HashMap;
import java.util.Map;

import org.dol.framework.logging.Logger;

public class JobMessage {

    public static final String PRODUCE_BY = "produce_by";
    public static final String SEND_RETRY_TIMES = "send_retry_times";
    public static final String CREATED_TIME_MS = "create_time";

    public static final int DESTINATION_TYPE_QUEUE = 1;
    public static final int DESTINATION_TYPE_TOPIC = 2;

    private String destinationName;
    private int destinationType;

    private String messageId;
    private String producedBy;
    private long priority;
    private int delaySeconds;
    private int timeToRun;
    private long timestamp;

    private Map<String, Object> properties;
    private Map<String, String> stats;
    private String body;

    public JobMessage() {
        setProperties(new HashMap<String, Object>());
    }

    public JobMessage(String producedBy, long priority, int delaySeconds, String body) {
        this();
        this.setProducedBy(producedBy);
        this.setPriority(priority);
        this.setDelaySeconds(delaySeconds);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getStats() {
        return stats;
    }

    public void setStats(Map<String, String> stats) {
        this.stats = stats;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public int getTimeToRun() {
        return timeToRun;
    }

    public void setTimeToRun(int timeToRun) {
        this.timeToRun = timeToRun;
    }

    public String getProducedBy() {
        return producedBy;
    }

    public void setProducedBy(String producedBy) {
        this.producedBy = producedBy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public int getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(int destinationType) {
        this.destinationType = destinationType;
    }

    public String getRid() {
        Object obj = this.getProperty(Logger.REQUEST_ID_KEY);
        return obj == null ? null : (String) obj;
    }

    public void setRid(String rid) {
        this.addProperty(Logger.REQUEST_ID_KEY, rid);
    }

    public void removeProperty(String key) {
        this.properties.remove(key);
    }

}
