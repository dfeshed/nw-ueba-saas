package presidio.output.processor.services;

import org.springframework.beans.factory.annotation.Autowired;
import presidio.monitoring.sdk.api.services.model.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by efratn on 16/01/2018.
 */
public class OutputMonitoringService {

    private static final String NUM_ANOMALY_EVENTS_METRIC_NAME = "number_of_anomaly_events";
    private static final String NUM_ACTIVE_USERS_LAST_DAY_METRIC_NAME = "active_users_last_day";
    private final String NUMBER_OF_ALERTS_METRIC_NAME = "number_of_alerts_created";
    private final String NUMBER_OF_USERS_METRIC_NAME = "number_of_users_created";
    private final String LAST_SMART_TIME_METRIC_NAME = "last_smart_time";

    @Autowired
    MetricCollectingService metricCollectingService;

    public void reportTotalAlertCount(int count, AlertEnums.AlertSeverity alertSeverity, String classification, Instant startDate) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.ALERT_CLASSIFICATION, classification);
        tags.put(MetricEnums.MetricTagKeysEnum.ALERT_SEVERITY, alertSeverity.name());
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
                setMetricUnit(MetricEnums.MetricUnitType.MILLI_SECOND).
                setMetricLogicTime(startDate).
                build());
    }

    public void reportTotalUsersCount(int count, Instant startDate) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(NUMBER_OF_USERS_METRIC_NAME).
                setMetricValue(count).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                setMetricLogicTime(startDate).
                build());
    }

    public void reportTotalAnomalyEvents(List<Alert> alerts, Instant startDate) {
        int eventsCount = 0;
        for (Alert alert: alerts) {
            eventsCount += alert.countRelatedEvents();
        }

        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(NUM_ANOMALY_EVENTS_METRIC_NAME).
                setMetricValue(eventsCount).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                setMetricLogicTime(startDate).
                build());
    }

    public void reportNumActiveUsersLastDay(int count, Instant logicalTime) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(NUM_ACTIVE_USERS_LAST_DAY_METRIC_NAME).
                setMetricValue(count).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                setMetricLogicTime(logicalTime).
                build());
    }
}
