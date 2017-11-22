package presidio.monitoring.records;


import org.apache.commons.collections.MapUtils;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Metric {

    private String name;
    private Map<MetricEnums.MetricValues, Number> value;
    private Instant time;
    private Instant logicTime;
    private Map<MetricEnums.MetricTagKeysEnum, String> tags;
    private boolean reportOneTime;

    public Metric(MetricBuilder metricBuilder) {
        this.name = metricBuilder.metricName;
        this.value = metricBuilder.metricValues;
        this.time = Instant.now();
        this.logicTime = metricBuilder.metricLogicTime;
        this.tags = metricBuilder.metricTags;
        this.reportOneTime = metricBuilder.metricReportOnce;
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

    public void setTime(Instant time) {
        this.time = time;
    }

    public void setTags(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        this.tags = tags;
    }

    public void addTag(MetricEnums.MetricTagKeysEnum key, String tag) {
        if (MapUtils.isEmpty(this.tags)) {
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

    public Instant getTime() {
        return time;
    }

    public Map<MetricEnums.MetricTagKeysEnum, String> getTags() {
        return tags;
    }

    public boolean isReportOneTime() {
        return reportOneTime;
    }

    public Instant getLogicTime() {
        return logicTime;
    }

    public void setLogicTime(Instant logicTime) {
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
            if (MapUtils.isEmpty(this.metricTags)) {
                this.metricTags = new HashMap<>();
            }
            metricTags.put(MetricEnums.MetricTagKeysEnum.UNIT, metricUnit.toString());
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
            if (MapUtils.isEmpty(this.metricTags)) {
                this.metricTags = new HashMap<>();
            }
            if (!metricTags.containsKey(MetricEnums.MetricTagKeysEnum.UNIT)) {
                metricTags.put(MetricEnums.MetricTagKeysEnum.UNIT, MetricEnums.MetricUnitType.NUMBER.toString());
            }
            return new Metric(this);
        }
    }

}