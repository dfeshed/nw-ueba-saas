package org.apache.flume;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.conf.MonitorDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


/**
 * Wrapper for presidioExternalMonitoringService to handle monitoring of single events processed by flume
 */
public class FlumePresidioExternalMonitoringService {

    private static Logger logger = LoggerFactory.getLogger(FlumePresidioExternalMonitoringService.class);
    public static final String NUMBER_OF_PROCESSED_EVENTS = "events_processed";
    private PresidioExternalMonitoringService presidioExternalMonitoringService;
    private Instant logicalHour;
    private String defaultSchema;
    private String flumeComponentType;
    private String flumeComponentInstannceId;

    /**
     * @param monitorDetails            - initiated instance of monitoring service, logicalHour, and default schema
     * @param flumeComponentType        - The type of flume component - I.E. SOURCE
     * @param flumeComponentInstannceId - the flume instance - instance name or other representive name
     */
    public FlumePresidioExternalMonitoringService(MonitorDetails monitorDetails, FlumeComponentType flumeComponentType, String flumeComponentInstannceId) {
        this.presidioExternalMonitoringService = monitorDetails.getPresidioExternalMonitoringService();
        this.logicalHour = monitorDetails.getLogicalhour();
        this.defaultSchema = monitorDetails.getSchema();
        this.flumeComponentType = flumeComponentType.name();
        this.flumeComponentInstannceId = flumeComponentInstannceId;
    }


    //**************** Expost the original method of the core external monitoring service
    public void reportCustomMetric(String metricName, Number value, Map<MetricEnums.MetricTagKeysEnum, String> tags, MetricEnums.MetricUnitType valueType, Instant logicTime) {
        presidioExternalMonitoringService.reportCustomMetric(metricName, value, tags, valueType, logicTime);
    }

    public void reportCustomMetricMultipleValues(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, MetricEnums.MetricUnitType valueType, Instant logicTime) {
        presidioExternalMonitoringService.reportCustomMetricMultipleValues(metricName, value, tags, valueType, logicTime);
    }

    public void manualExportMetrics() {
        presidioExternalMonitoringService.manualExportMetrics();
    }

    /**
     * Single event metric success
     *
     * @param amount -  number of events to update
     */
    public void reportSuccessEventMetric(int amount) {
        reportMetric(NUMBER_OF_PROCESSED_EVENTS, false, null, MetricEnums.MetricValues.SUCCESS_EVENTS, amount);
    }


    /**
     * * @param amount -  number of events to update
     *
     * @param errorKey - the reason of failure
     * @param amount   -  number of events to update
     */
    public void reportFailedEventMetric(String errorKey, int amount) {
        if (StringUtils.isBlank(errorKey)) {
            throw new RuntimeException("Metric error tag cannot be empty");
        }
        reportMetric(NUMBER_OF_PROCESSED_EVENTS, true, errorKey, MetricEnums.MetricValues.FAILED_EVENTS, amount);
    }

    /**
     * Update total retrieved events
     *
     * @param amount -  number of events to update
     */
    public void reportTotalEventMetric(int amount) {

        reportMetric(NUMBER_OF_PROCESSED_EVENTS, true, null, MetricEnums.MetricValues.TOTAL_EVENTS, amount);
    }

    /**
     * Report event metrics which are not single event (Page, webhook, etc...)
     *
     * @param metricName
     * @param value
     * @param amount
     */
    public void reportSuccessAndTotalMetric(String metricName, MetricEnums.MetricValues value, int amount) {
        reportMetric(metricName, false, null, value, amount);
    }

    /**
     * Report event metrics which are not single event (Page, webhook, etc...)
     *
     * @param metricName
     * @param errorKey   - error reason
     * @param value
     * @param amount
     */
    public void reportFailedMetric(String metricName, String errorKey, MetricEnums.MetricValues value, int amount) {
        if (StringUtils.isBlank(errorKey)) {
            throw new RuntimeException("Metric error tag cannot be empty");
        }
        reportMetric(metricName, true, errorKey, value, amount);
    }

    /**
     * Report metric - use process schema and logicalHour
     *
     * @param metricName
     * @param isFailure
     * @param statusTag
     * @param value
     * @param amount
     */

    protected void reportMetric(String metricName, boolean isFailure, String statusTag, MetricEnums.MetricValues value, int amount) {
        reportMetric(metricName, isFailure, statusTag, value, amount, null, null);
    }

    /**
     * Report metric - can get specific logical hour and schema
     *
     * @param metricName
     * @param isFailure
     * @param statusTag
     * @param value
     * @param amount
     * @param schema
     * @param logicalHour
     */
    protected void reportMetric(String metricName, boolean isFailure, String statusTag, MetricEnums.MetricValues value, int amount, String schema, Instant logicalHour) {
        if (amount <= 0 || value == null) {
            return;
        }


        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(value, amount);

        Map<MetricEnums.MetricTagKeysEnum, String> tags = getBasicTags(isFailure, statusTag, schema);

        if (logicalHour == null) {
            logicalHour = this.logicalHour;
        }

        this.reportCustomMetricMultipleValues(metricName, values, tags, MetricEnums.MetricUnitType.NUMBER, logicalHour);
    }


    /**
     * Build tag with schema, status, and component name so we will be able to filter by any of them
     *
     * @param isFailure - if this monitoring item represent failure to read or write page or events. If ture- the resason must be supplied in the status tag
     * @param statusTag
     * @param schema
     * @return
     */
    protected Map<MetricEnums.MetricTagKeysEnum, String> getBasicTags(boolean isFailure, String statusTag, String schema) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();

        //Set Schema tag if possible
        if (StringUtils.isBlank(schema)) {
            schema = defaultSchema;
        }
        if (StringUtils.isNotBlank(schema)) {
            tags.put(MetricEnums.MetricTagKeysEnum.SCHEMA, schema);
        }
        //Set Failure Tag if relevant
        if (isFailure) {
            tags.put(MetricEnums.MetricTagKeysEnum.FAILURE_REASON, statusTag);
        }


        if (StringUtils.isNotBlank(flumeComponentType)) {
            tags.put(MetricEnums.MetricTagKeysEnum.FLUME_COMPONENT_TYPE, flumeComponentType);
        }

        if (StringUtils.isNotBlank(flumeComponentInstannceId)) {
            tags.put(MetricEnums.MetricTagKeysEnum.FLUME_COMPONENT_INSTANCE_ID, flumeComponentInstannceId);
        }
        return tags;
    }


    public enum FlumeComponentType {
        SOURCE, SINK, INTERCEPTOR
    }
}
