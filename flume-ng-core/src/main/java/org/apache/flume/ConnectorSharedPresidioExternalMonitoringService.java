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
 * Static service to share one monitor service instance between all the flume parts (source, sink, interceptor
 */
public class ConnectorSharedPresidioExternalMonitoringService{


    public static final String NUMBER_OF_PROCESSED_EVENTS = "events_processed";
    private PresidioExternalMonitoringService presidioExternalMonitoringService;
    private Instant logicalHour;
    private String defaultSchema;
    private String origin;

    /**
     *
     * @param monitorDetails  - initiated instance of monitoring service, logicalHour, and default schema
     * @param origin - the "reporter" - source, interceptor, sink - usually the class name
     */
    public ConnectorSharedPresidioExternalMonitoringService(MonitorDetails monitorDetails, String origin) {
        this.presidioExternalMonitoringService = monitorDetails.getPresidioExternalMonitoringService();
        this.logicalHour = monitorDetails.getLogicalhour();
        this.defaultSchema = monitorDetails.getSchema();

        this.origin = origin;


    }

    private static Logger logger = LoggerFactory.getLogger(ConnectorSharedPresidioExternalMonitoringService.class);




     //**************** Expost the original method of the core external monitoring service
    public void reportCustomMetric(String metricName, Number value, Map<MetricEnums.MetricTagKeysEnum, String> tags, MetricEnums.MetricUnitType valueType, Instant logicTime){
        presidioExternalMonitoringService.reportCustomMetric(metricName,value,tags,valueType,logicTime);
    }

    public void reportCustomMetricMultipleValues(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, MetricEnums.MetricUnitType valueType, Instant logicTime){
        presidioExternalMonitoringService.reportCustomMetricMultipleValues(metricName,value,tags,valueType,logicTime);
    }

    public void manualExportMetrics(){
        presidioExternalMonitoringService.manualExportMetrics();
    }

    /**
     * Single event metric - the most common:
     */

    public void reportSuccessEventMetric(int amount) {
        reportMetric(NUMBER_OF_PROCESSED_EVENTS,false,null, MetricEnums.MetricValues.SUCCESS_EVENTS,amount);
    }


    public  void reportFailedEventMetric(String errorKey, int amount) {
        if (StringUtils.isBlank(errorKey)){
            throw new RuntimeException("Metric error tag cannot be empty");
        }
        reportMetric(NUMBER_OF_PROCESSED_EVENTS,true,errorKey, MetricEnums.MetricValues.FAILED_EVENTS,amount);
    }

    public  void reportTotalEventMetric(int amount) {

        reportMetric(NUMBER_OF_PROCESSED_EVENTS,true,null, MetricEnums.MetricValues.TOTAL_EVENTS,amount);
    }

    /**
     * Report event metrics which are not single event
     * @param metricName
     * @param value
     * @param amount
     */
    public void reportSuccessAndTotalMetric(String metricName, MetricEnums.MetricValues value, int amount) {
        reportMetric(metricName,false,null,value,amount);
    }


    public void reportFailedMetric(String metricName, String errorKey, MetricEnums.MetricValues value, int amount) {
        if (StringUtils.isBlank(errorKey)){
            throw new RuntimeException("Metric error tag cannot be empty");
        }
        reportMetric(metricName,true,errorKey,value,amount);
    }

    protected void reportMetric(String metricName, boolean isFailure, String statusTag, MetricEnums.MetricValues value, int amount){
        reportMetric(metricName,isFailure,statusTag,value,amount,null,null);
    }

    protected void reportMetric(String metricName, boolean isFailure, String statusTag, MetricEnums.MetricValues value, int amount, String schema, Instant logicalHour) {
        if(amount<=0 || value == null){
            return;
        }


        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(value,amount);

        Map<MetricEnums.MetricTagKeysEnum, String> tags = getBasicTags(isFailure, statusTag, schema);

        this.reportCustomMetricMultipleValues(metricName,values,tags, MetricEnums.MetricUnitType.NUMBER,logicalHour);
    }

    protected Map<MetricEnums.MetricTagKeysEnum, String> getBasicTags(boolean isFailure, String statusTag, String schema) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags= new HashMap<>();

        //Set Schema tag if possible
        if (StringUtils.isBlank(schema)){
            schema = defaultSchema;
        }
        if (StringUtils.isNotBlank(schema)){
            tags.put(MetricEnums.MetricTagKeysEnum.SCHEMA,schema);
        }
        //Set Failure Tag if relevant
        if (isFailure){
            tags.put(MetricEnums.MetricTagKeysEnum.FAILURE_REASON,statusTag);
        }


        if (StringUtils.isNotBlank(origin)){
            tags.put(MetricEnums.MetricTagKeysEnum.GROUP_NAME,origin);
        }
        return tags;
    }
}
