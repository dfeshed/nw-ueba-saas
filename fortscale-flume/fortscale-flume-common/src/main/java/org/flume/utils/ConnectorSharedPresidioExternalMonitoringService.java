package org.flume.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.sdk.impl.factory.PresidioExternalMonitoringServiceFactory;

import java.time.Instant;
import java.util.Map;


/**
 * Static service to share one monitor service instance between all the flume parts (source, sink, interceptor
 */
public enum ConnectorSharedPresidioExternalMonitoringService implements PresidioExternalMonitoringService{
    COLLECTOR_INSTANCE("collector");


    private static Logger logger = LoggerFactory.getLogger(ConnectorSharedPresidioExternalMonitoringService.class);
    private PresidioExternalMonitoringServiceFactory presidioExternalMonitoringServiceFactory;
    private PresidioExternalMonitoringService presidioExternalMonitoringService=null;

    /**
     * Init singleton instance of the connector manager
      * @param appName
     * @throws RuntimeException
     */
    ConnectorSharedPresidioExternalMonitoringService(String appName) throws RuntimeException {
        try {
            init(appName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Inits the presidioExternalMonitoringService
     * @param appName
     * @throws Exception
     */
    private void init(String appName) throws Exception{
        presidioExternalMonitoringServiceFactory = new PresidioExternalMonitoringServiceFactory();
        try {
            presidioExternalMonitoringService= presidioExternalMonitoringServiceFactory.createPresidioExternalMonitoringService(appName);
        }
        catch (Exception e){
            logger.error("Cannot load external monitoring service");
            throw e;
        }

        logger.info("New Monitoring Service has initiated");
    }

    /**
     * Destroy is actual only export the metrics
     */
    public void destroy(){
        logger.info("Monitoring Service is going down");
        logger.info("Monitoring Service export metrics before going down");
        presidioExternalMonitoringService.manualExportMetrics();
        logger.info("Monitoring Service went down");

    }



     //**************** Expost the original method of the core external monitoring service

    @Override
    public void reportCustomMetric(String metricName, Number value, Map<MetricEnums.MetricTagKeysEnum, String> tags, MetricEnums.MetricUnitType valueType, Instant logicTime){
        presidioExternalMonitoringService.reportCustomMetric(metricName,value,tags,valueType,logicTime);
    }

    public void reportCustomMetricMultipleValues(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, MetricEnums.MetricUnitType valueType, Instant logicTime){
        presidioExternalMonitoringService.reportCustomMetricMultipleValues(metricName,value,tags,valueType,logicTime);
    }

    public void manualExportMetrics(){
        presidioExternalMonitoringService.manualExportMetrics();
    }
}
