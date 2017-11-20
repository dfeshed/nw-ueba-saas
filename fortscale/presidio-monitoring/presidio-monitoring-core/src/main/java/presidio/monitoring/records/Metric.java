package presidio.monitoring.records;


import org.springframework.util.ObjectUtils;
import presidio.monitoring.enums.MetricEnums;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    public void addTag(MetricEnums.MetricTagKeysEnum key, String tag) {
        if (ObjectUtils.isEmpty(this.tags)) {
            this.tags = new HashMap<>();
        }
        this.tags.put(key, tag);
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

    public static class MetricBuilder {

        private String metricName;
        private Map<MetricEnums.MetricValues, Number> metricValues;
        private Map<MetricEnums.MetricTagKeysEnum, String> metricTags;
        private Instant metricLogicTime;
        private boolean metricReportOnce;
        private MetricEnums.MetricUnitType metricUnit;

        public MetricBuilder() {
        }

        public Metric.MetricBuilder setMetricName(String metricName) {
            this.metricName = metricName;
            return this;
        }

        public Metric.MetricBuilder setMetricUnit(MetricEnums.MetricUnitType metricUnit) {
            this.metricUnit = metricUnit;
            return this;
        }

        public Metric.MetricBuilder setMetricMultipleValues(Map<MetricEnums.MetricValues, Number> metricValues) {
            this.metricValues = metricValues;
            return this;
        }

        public Metric.MetricBuilder setMetricValue(Number metricValue) {
            Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
            map.put(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE, metricValue);
            this.metricValues = map;
            return this;
        }

        public Metric.MetricBuilder setMetricTags(Map<MetricEnums.MetricTagKeysEnum, String> metricTags) {
            this.metricTags = metricTags;
            return this;
        }

        public Metric.MetricBuilder setMetricLogicTime(Instant metricLogicTime) {
            this.metricLogicTime = metricLogicTime;
            return this;
        }

        public Metric.MetricBuilder setMetricReportOnce(boolean metricReportOnce) {
            this.metricReportOnce = metricReportOnce;
            return this;
        }

        public Metric build() {
            Date date = null;
            if (ObjectUtils.isEmpty(this.metricTags)) {
                this.metricTags = new HashMap<>();
            }
            if (ObjectUtils.isEmpty(this.metricUnit)) {
                this.metricUnit = MetricEnums.MetricUnitType.fromValue("metric_type");
            }
            if (!ObjectUtils.isEmpty(this.metricLogicTime)) {
                date = Date.from(metricLogicTime);
            }
            metricTags.put(MetricEnums.MetricTagKeysEnum.UNIT, metricUnit.toString());
            return new Metric(metricName, metricValues, date, metricTags, metricReportOnce);
        }

    }
}
