package presidio.output.processor.services;

import org.springframework.beans.factory.annotation.Autowired;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.output.domain.records.alerts.AlertEnums;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by efratn on 16/01/2018.
 */
public class OutputMonitoringService {

    private final String NUMBER_OF_ALERTS_METRIC_NAME = "number_of_alerts_created";
    private final String ALERT_WITH_SEVERITY_METRIC_PREFIX = "alert_created_with_severity.";
    private final String ALERT_WITH_CLASSIFICATION_METRIC_PREFIX = "alert_created_with_classification.";
    private final String LAST_SMART_TIME_METRIC_NAME = "last_smart_time";

    @Autowired
    MetricCollectingService metricCollectingService;

    public void reportAlertWithSeverity(int count, AlertEnums.AlertSeverity alertSeverity, Instant logicalTime ) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(ALERT_WITH_SEVERITY_METRIC_PREFIX + alertSeverity.name()).
                setMetricValue(count).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                setMetricLogicTime(logicalTime).
                build());
    }

    public void reportAlertWithClassification(int count, String classification, Instant logicalTime ) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(ALERT_WITH_CLASSIFICATION_METRIC_PREFIX + classification).
                setMetricValue(count).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                setMetricLogicTime(logicalTime).
                build());
    }

    public void reportTotalAlertCount(int count, Instant startDate) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(NUMBER_OF_ALERTS_METRIC_NAME).
                setMetricValue(count).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                setMetricLogicTime(startDate).
                build());
    }

    public void reportLastSmartTimeProcessed(long value, Instant startDate) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(LAST_SMART_TIME_METRIC_NAME).
                setMetricValue(value).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                setMetricLogicTime(startDate).
                build());
    }
}
