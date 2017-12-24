package org.apache.flume.conf;

import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

import java.time.Instant;

/**
 * Created by shays on 24/12/2017.
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

    public void setLogicalhour(Instant logicalhour) {
        this.logicalhour = logicalhour;
    }

    public PresidioExternalMonitoringService getPresidioExternalMonitoringService() {
        return presidioExternalMonitoringService;
    }

    public void setPresidioExternalMonitoringService(PresidioExternalMonitoringService presidioExternalMonitoringService) {
        this.presidioExternalMonitoringService = presidioExternalMonitoringService;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
