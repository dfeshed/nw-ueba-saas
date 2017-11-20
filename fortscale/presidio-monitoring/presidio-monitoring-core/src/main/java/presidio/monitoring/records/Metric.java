package presidio.monitoring.records;


import presidio.monitoring.enums.MetricEnums;

import java.util.*;

public class Metric {

    private String name;
    private Map<MetricEnums.MetricValues, Number> value;
    private Date time;
    private Date logicTime;
    private Map<MetricEnums.MetricTagKeysEnum, String> tags;
    private boolean reportOneTime;

    public Metric(String name, Map<MetricEnums.MetricValues, Number> value, Date logicTime, Map<MetricEnums.MetricTagKeysEnum, String> tags, boolean reportOneTime) {
        this.name = name;
        this.value = value;
        this.time = new Date();
        this.logicTime = logicTime;
        this.tags = tags;
        this.reportOneTime = reportOneTime;
    }

    public Metric(String name, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, boolean reportOneTime) {
        this(name, value, null, tags, reportOneTime);
    }

    public Metric(String name, Map<MetricEnums.MetricValues, Number> value, boolean reportOneTime) {
        this(name, value, new HashMap<>(), reportOneTime);
    }

    public Metric(String name, Map<MetricEnums.MetricValues, Number> value) {
        this(name, value, false);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Map<MetricEnums.MetricValues, Number> value) {
        this.value = value;
    }

    public void addValue(Number value, MetricEnums.MetricValues name) {
        this.value.put(name, value);
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setTags(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        this.tags = tags;
    }

    public void setReportOneTime(boolean reportOneTime) {
        this.reportOneTime = reportOneTime;
    }

    public String getName() {

        return name;
    }

    public Map<MetricEnums.MetricValues, Number> getValue() {
        return value;
    }

    public Date getTime() {
        return time;
    }

    public Map<MetricEnums.MetricTagKeysEnum, String> getTags() {
        return tags;
    }

    public boolean isReportOneTime() {
        return reportOneTime;
    }

    public Date getLogicTime() {
        return logicTime;
    }

    public void setLogicTime(Date logicTime) {
        this.logicTime = logicTime;
    }
}
