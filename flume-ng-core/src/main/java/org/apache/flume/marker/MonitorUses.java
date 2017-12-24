package org.apache.flume.marker;

import org.apache.flume.conf.MonitorDetails;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

import java.time.Instant;

/**
 * Created by shays on 24/12/2017.
 */
public interface MonitorUses {

    void setMonitorDetails(MonitorDetails monitorDetails);

}
