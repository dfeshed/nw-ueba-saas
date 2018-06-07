package presidio.output.processor.services;

import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.sdk.api.services.model.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by efratn on 16/01/2018.
 */
public class OutputMonitoringService {

    private static final String NUM_ANOMALY_EVENTS_METRIC_NAME = "number_of_anomaly_events";
    private final String NUMBER_OF_ALERTS_METRIC_NAME = "number_of_alerts_created";
    private final String INPUT_TOTAL_EVENTS_PROCESSED_METRIC_NAME = "total_events_processed";
    private final String NUMBER_OF_USERS_METRIC_NAME = "number_of_users_created";
    private final String LAST_SMART_TIME_METRIC_NAME = "last_smart_time";
    private final String OUTPUT_METRIC_NAME_PREFIX = "output-core.";
    private final String INPUT_METRIC_NAME_PREFIX = "input-core.";
    public static final String EVENTS_PROCESSED_COUNT_DAILY_METRIC_NAME = "events_processed_count_daily";
    public static final String OUTPUT_INDICATORS_COUNT_DAILY_METRIC_NAME = "alert_indicators_count_daily";
    public static final String ADE_INDICATORS_COUNT_DAILY_METRIC_NAME = "smart_indicators_count_daily";
    public static final String INDICATORS_COUNT_HOURLY_METRIC_NAME = "alert_indicators_count_hourly";
    public static final String NUM_ACTIVE_USERS_LAST_DAY_METRIC_NAME = "active_users_count_last_day";
    public static final String SMARTS_COUNT_LAST_DAY_METRIC_NAME = "smarts_count_last_day";
    public static final String ALERTS_COUNT_LAST_DAY_METRIC_NAME = "alerts_count_last_day";


    private final AdeManagerSdk adeManagerSdk;
    private final PresidioMetricPersistencyService metricPersistencyService;

    public OutputMonitoringService(AdeManagerSdk adeManagerSdk, PresidioMetricPersistencyService metricPersistencyService) {
        this.adeManagerSdk = adeManagerSdk;
        this.metricPersistencyService = metricPersistencyService;
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
        Metric metric = new Metric.MetricBuilder().setMetricName(metricName)
                .setMetricValue(value)
                .setMetricTags(tags)
                .setMetricUnit(MetricEnums.MetricUnitType.NUMBER)
                .setMetricLogicTime(logicalTime)
                .setMetricReportOnce(true)
                .build();
        metricCollectingService.addMetric(metric);
    }

    public void reportDailyMetrics() {
        //----Report daily metric- number of active users in the last 24 hours---
        //active user = user with smart (smart score >= 0)
        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(Duration.ofHours(24));
        int distinctSmartUsers = adeManagerSdk.getDistinctSmartUsers(new TimeRange(startDate, endDate));
        reportNumericMetric(NUM_ACTIVE_USERS_LAST_DAY_METRIC_NAME, distinctSmartUsers, startDate);

        //----Report daily metric- num of smarts---
        //read from elastic all hourly metrics named "smart.scoring" with value "amountOfScored" for the last day
        List<MetricDocument> scoringHourlyMetrics = metricPersistencyService.getMetricsByNamesAndTime(Collections.singleton("smart.scoring"), startDate, endDate);
        List<Number> smartsCountHourlyValues = scoringHourlyMetrics.stream().
                map(metricDocument -> metricDocument.getValue().get(MetricEnums.MetricValues.AMOUNT_OF_SCORED))
                .collect(Collectors.toList());
        int sumOfHourlySmartsCount = smartsCountHourlyValues.stream().mapToInt(Number::intValue).sum();
        reportNumericMetric(SMARTS_COUNT_LAST_DAY_METRIC_NAME, sumOfHourlySmartsCount, startDate);

        //----Report daily metric- num of alerts---
        List<MetricDocument> alertCountHourlyMetrics = metricPersistencyService.getMetricsByNamesAndTime(Collections.singleton(OUTPUT_METRIC_NAME_PREFIX + NUMBER_OF_ALERTS_METRIC_NAME), startDate, endDate);
        List<Number> alertCountHourlyValues = alertCountHourlyMetrics.stream().
                map(metricDocument -> metricDocument.getValue().get(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE))
                .collect(Collectors.toList());
        int alertCount = alertCountHourlyValues.stream().mapToInt(Number::intValue).sum();
        reportNumericMetric(ALERTS_COUNT_LAST_DAY_METRIC_NAME, alertCount, startDate);

        //----Report daily metric- indicators count (output)---
        List<MetricDocument> indicatorCountHourlyMetrics = metricPersistencyService.getMetricsByNamesAndTime(Collections.singleton(OUTPUT_METRIC_NAME_PREFIX + INDICATORS_COUNT_HOURLY_METRIC_NAME), startDate, endDate);
        List<Number> indicatorCountHourlyValues = indicatorCountHourlyMetrics.stream().
                map(metricDocument -> metricDocument.getValue().get(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE))
                .collect(Collectors.toList());
        int indicatorsCount = indicatorCountHourlyValues.stream().mapToInt(Number::intValue).sum();
        reportNumericMetric(OUTPUT_INDICATORS_COUNT_DAILY_METRIC_NAME, indicatorsCount, startDate);

        //----Report daily metric- events processed count (input)---
        List<MetricDocument> eventCountHourlyMetrics = metricPersistencyService.getMetricsByNamesAndTime(Collections.singleton(INPUT_METRIC_NAME_PREFIX + INPUT_TOTAL_EVENTS_PROCESSED_METRIC_NAME), startDate, endDate);
        List<Number> eventCountHourlyValues = eventCountHourlyMetrics.stream().
                map(metricDocument -> metricDocument.getValue().get(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE))
                .collect(Collectors.toList());
        int eventProcessedNum = eventCountHourlyValues.stream().mapToInt(Number::intValue).sum();
        reportNumericMetric(EVENTS_PROCESSED_COUNT_DAILY_METRIC_NAME, eventProcessedNum, startDate);

        //----Report daily metric- indicators count (ADE)---
        List<MetricDocument> scoreIndicatorCountHourlyMetrics = metricPersistencyService.getMetricsByNamesAndTime(Collections.singleton("score-aggregation.AggregationRecordsCreator"), startDate, endDate);
        List<Number> scoreIndicatorCountHourlyValues = scoreIndicatorCountHourlyMetrics.stream().
                map(metricDocument -> metricDocument.getValue().get(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE))
                .collect(Collectors.toList());
        int smartsIndicatorsNum = scoreIndicatorCountHourlyValues.stream().mapToInt(Number::intValue).sum();

        List<MetricDocument> featureIndicatorCountHourlyMetrics = metricPersistencyService.getMetricsByNamesAndTime(Collections.singleton("feature-aggregation.AggregationRecordsCreator"), startDate, endDate);
        List<Number> featureIndicatorCountHourlyValues = featureIndicatorCountHourlyMetrics.stream().
                map(metricDocument -> metricDocument.getValue().get(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE))
                .collect(Collectors.toList());
        smartsIndicatorsNum += featureIndicatorCountHourlyValues.stream().mapToInt(Number::intValue).sum();
        reportNumericMetric(ADE_INDICATORS_COUNT_DAILY_METRIC_NAME, smartsIndicatorsNum, startDate);

    }

}
