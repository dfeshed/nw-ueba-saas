package org.apache.flume.conf;

import org.apache.flume.Context;
import org.apache.flume.marker.MonitorInitiator;

import java.util.Map;

/**
 * Flume Context supports only string-string proverties (string key, string value)
 * This context is adding the monitor details to the context
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
