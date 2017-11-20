package presidio.monitoring.factory;


import org.springframework.util.ObjectUtils;
import presidio.monitoring.enums.MetricEnums;
import presidio.monitoring.records.Metric;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class PresidioMetricFactory {

    private static String applicationName;

    public PresidioMetricFactory(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
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

        public MetricBuilder setMetricName(String metricName) {
            this.metricName = metricName;
            return this;
        }

        public MetricBuilder setMetricUnit(MetricEnums.MetricUnitType metricUnit) {
            this.metricUnit = metricUnit;
            return this;
        }

        public MetricBuilder setMetricMultipleValues(Map<MetricEnums.MetricValues, Number> metricValues) {
            this.metricValues = metricValues;
            return this;
        }

        public MetricBuilder setMetricValue(Number metricValue) {
            Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
            map.put(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE, metricValue);
            this.metricValues = map;
            return this;
        }

        public MetricBuilder setMetricTags(Map<MetricEnums.MetricTagKeysEnum, String> metricTags) {
            this.metricTags = metricTags;
            return this;
        }

        public MetricBuilder setMetricLogicTime(Instant metricLogicTime) {
            this.metricLogicTime = metricLogicTime;
            return this;
        }

        public MetricBuilder setMetricReportOnce(boolean metricReportOnce) {
            this.metricReportOnce = metricReportOnce;
            return this;
        }

        public Metric build() {
            if (ObjectUtils.isEmpty(this.metricTags)) {
                Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
                this.metricTags = tags;
            }
            metricTags.put(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
            metricTags.put(MetricEnums.MetricTagKeysEnum.UNIT, metricUnit.toString());
            return new Metric(metricName, metricValues, Date.from(metricLogicTime), metricTags, metricReportOnce);
        }

    }
}
