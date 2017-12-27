package org.apache.flume.conf;

import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

import java.time.Instant;

/**
 * Context to contain single instance of presidioExternalMonitoringService
 * In addition, this context will contain the schema and logical hour (if relevant, the adapter always contain schema and logical hour, the collector is not)
 */
public class MonitorDetails {

    private Instant logicalhour;
    private PresidioExternalMonitoringService presidioExternalMonitoringService;
    private String schema;

    public MonitorDetails(Instant logicalhour, PresidioExternalMonitoringService presidioExternalMonitoringService, String schema) {
        this.logicalhour = logicalhour;
        this.presidioExternalMonitoringService = presidioExternalMonitoringService;
        this.schema = schema;
    }

    public Instant getLogicalhour() {
        return logicalhour;
    }


    public PresidioExternalMonitoringService getPresidioExternalMonitoringService() {
        return presidioExternalMonitoringService;
    }


    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
