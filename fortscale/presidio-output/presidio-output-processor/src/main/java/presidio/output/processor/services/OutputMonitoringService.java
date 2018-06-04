package presidio.output.processor.services;

import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.monitoring.sdk.api.services.model.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by efratn on 16/01/2018.
 */
public class OutputMonitoringService {

    private static final String NUM_ANOMALY_EVENTS_METRIC_NAME = "number_of_anomaly_events";
    private final String NUMBER_OF_ALERTS_METRIC_NAME = "number_of_alerts_created";
    private final String NUMBER_OF_USERS_METRIC_NAME = "number_of_users_created";
    private final String LAST_SMART_TIME_METRIC_NAME = "last_smart_time";
    private static final String EVENTS_PROCESSED_COUNT_DAILY_METRIC_NAME = "events_processed_count_daily";
    private static final String OUTPUT_INDICATORS_COUNT_DAILY_METRIC_NAME = "alert_indicators_count_daily";
    private static final String ADE_INDICATORS_COUNT_DAILY_METRIC_NAME = "smart_indicators_count_daily";
    public static final String INDICATORS_COUNT_HOURLY_METRIC_NAME = "alert_indicators_count_hourly";
    private static final String NUM_ACTIVE_USERS_LAST_DAY_METRIC_NAME = "active_users_count_last_day";
    private static final String SMARTS_COUNT_LAST_DAY_METRIC_NAME = "smarts_count_last_day";
    private static final String ALERTS_COUNT_LAST_DAY_METRIC_NAME = "alerts_count_last_day";

    private final AdeManagerSdk adeManagerSdk;

    public OutputMonitoringService(AdeManagerSdk adeManagerSdk) {
        this.adeManagerSdk = adeManagerSdk;
    }

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

    public void reportNumericMetric(String metricName, int value, Instant logicalTime) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(metricName).
                setMetricValue(value).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                setMetricLogicTime(logicalTime).
                build());
    }

    public void reportDailyMetrics() {
        //calculate number of active users in the last 24 hours
        //active user = user with smart (smart score >= 0)
        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(Duration.ofHours(24));
        int distinctSmartUsers = adeManagerSdk.getDistinctSmartUsers(new TimeRange(startDate, endDate));
        reportNumericMetric(NUM_ACTIVE_USERS_LAST_DAY_METRIC_NAME, distinctSmartUsers, startDate);

        //report daily metric- num of smarts:
        //TODO get from elastic all hourly metrics named "smart.scoring" with value "amountOfScored" for the last day
        int smartRecordsCount = 0;
        reportNumericMetric(SMARTS_COUNT_LAST_DAY_METRIC_NAME, smartRecordsCount, startDate);

        //report daily metric- num of alerts
        //TODO get from elastic all hourly metrics named "number_of_alerts_created" for the last day
        int alertCount = 0;
        reportNumericMetric(ALERTS_COUNT_LAST_DAY_METRIC_NAME, alertCount, startDate);

        //report daily metric- indicators count (output)
        //TODO get from elastic all hourly metrics named "indicators_count_hourly"
        int indicatorsCount = 0;
        reportNumericMetric(OUTPUT_INDICATORS_COUNT_DAILY_METRIC_NAME, indicatorsCount, startDate);

        //report daily metric- events processed count (input)
        //TODO get from elastic all hourly metrics named "total_events_processed"
        int eventProcessedNum = 0;
        reportNumericMetric(EVENTS_PROCESSED_COUNT_DAILY_METRIC_NAME, eventProcessedNum, startDate);

        //report daily metric- indicators count (ADE)
        //TODO get from elastic all metrics named "score-aggregation.AggregationRecordsCreator" and "feature-aggregation.AggregationRecordsCreator" and take value.AGGREGATIONS value from it
        int smartsIndicatorsNum = 0;
        reportNumericMetric(ADE_INDICATORS_COUNT_DAILY_METRIC_NAME, smartsIndicatorsNum, startDate);

    }

}
