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
public enum ConnectorSharedPresidioMonitorHelper implements PresidioExternalMonitoringService{
    INSTANCE;

//    private static ConnectorSharedPresidioMonitorHelper connectorSharedPresidioMonitorHelper;

    private static Logger logger = LoggerFactory.getLogger(ConnectorSharedPresidioMonitorHelper.class);
    private PresidioExternalMonitoringServiceFactory presidioExternalMonitoringServiceFactory;
    private PresidioExternalMonitoringService presidioExternalMonitoringService=null;

    ConnectorSharedPresidioMonitorHelper() throws RuntimeException {
        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



    private void init() throws Exception{
        presidioExternalMonitoringServiceFactory = new PresidioExternalMonitoringServiceFactory();
        try {
            presidioExternalMonitoringService= presidioExternalMonitoringServiceFactory.createPresidioExternalMonitoringService("collector");
        }
        catch (Exception e){
            logger.error("Cannot load external monitoring service");
            throw e;
        }

        logger.info("New Monitoring Service has initiated");
    }

    public void destroy(){
        logger.info("Monitoring Service is going down");
        logger.info("Monitoring Service export metrics before going down");
        presidioExternalMonitoringService.manualExportMetrics();
        logger.info("Monitoring Service went down");

    }




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
