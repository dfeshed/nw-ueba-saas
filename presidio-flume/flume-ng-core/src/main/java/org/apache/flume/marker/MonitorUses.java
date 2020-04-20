package org.apache.flume.marker;

import org.apache.flume.conf.MonitorDetails;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

import java.time.Instant;

/**
 * A marker interfaces.
 * Sinks, and interceptors which implement this interface state that they need to get monitor details instance from the context
 */
public interface MonitorUses {

    void setMonitorDetails(MonitorDetails monitorDetails);

}
