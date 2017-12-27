package org.apache.flume.marker;

import org.apache.flume.conf.MonitorDetails;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

import java.time.Instant;

/**
 * Single point of monitor details instance in the agent.
 * Each agent should have one source, and if this source implement MonitorInitiator the agent retrive the MonitorInitiator and
 * set it to all Sinks and Interceptors which implement MonitorUses
 */
public interface MonitorInitiator {

    MonitorDetails getMonitorDetails();
}
