package org.apache.flume.conf;

import org.apache.flume.Context;
import org.apache.flume.marker.MonitorInitiator;
import org.apache.flume.marker.MonitorUses;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

import java.time.Instant;
import java.util.Map;

/**
 * Created by shays on 24/12/2017.
 */
public class MonitorableContext extends Context implements MonitorInitiator{

    private MonitorDetails monitorDetails;

    public MonitorableContext(Map<String, String> paramters, MonitorDetails monitorDetails) {
        super(paramters);
        this.monitorDetails = monitorDetails;

    }


    public MonitorDetails getMonitorDetails() {
        return monitorDetails;
    }
}
