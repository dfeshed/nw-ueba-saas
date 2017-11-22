package presidio.monitoring.sdk.impl.services;


import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PresidioExternalMonitoringServiceImpl implements PresidioExternalMonitoringService {

    private MetricCollectingService metricCollectingService;


    public PresidioExternalMonitoringServiceImpl(MetricCollectingService metricCollectingService) {
        this.metricCollectingService = metricCollectingService;
    }

    /**
     * This method is used to add an application metric.
     *
     * @param metricName - metricName is the name of the metric.
     * @param value      - value is the value of the collected metric can be int , double , long.
     * @param tags       - tags is Map<MetricEnums.MetricTagKeysEnum, String> of tags that will help filter or explain the metric.
     * @param valueType  - valueType explains the value of the metric, value can be megabyte , date ....
     * @param logicTime  - logicTime is the time of the event processed and not machine time.
     */
    @Override
    public void reportCustomMetric(String metricName, Number value, Map<MetricEnums.MetricTagKeysEnum, String> tags, MetricEnums.MetricUnitType valueType, Instant logicTime) {
        Map<MetricEnums.MetricValues, Number> valuesMap = new HashMap<>();
        valuesMap.put(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE, value);
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(metricName).
                setMetricMultipleValues(valuesMap).
                setMetricTags(tags).
                setMetricUnit(valueType).
                setMetricLogicTime(logicTime).
                build());
    }

    /**
     * This method is used to add an application metric.
     *
     * @param metricName - metricName is the name of the metric.
     * @param value      - value is Map<MetricEnums.MetricValues, Number> of several numbers that are related to one metric, metric can be int , double , long.
     * @param tags       - tags is Map<MetricEnums.MetricTagKeysEnum, String> of tags that will help filter or explain the metric.
     * @param valueType  - valueType explains the value of the metric, value can be megabyte , date ....
     * @param logicTime  - logicTime is the time of the event processed and not machine time.
     */
    @Override
    public void reportCustomMetricMultipleValues(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, MetricEnums.MetricUnitType valueType, Instant logicTime) {
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(metricName).
                setMetricMultipleValues(value).
                setMetricTags(tags).
                setMetricUnit(valueType).
                setMetricLogicTime(logicTime).
                build());

    }
}
