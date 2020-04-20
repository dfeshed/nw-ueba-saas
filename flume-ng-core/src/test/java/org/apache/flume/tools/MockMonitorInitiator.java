package org.apache.flume.tools;

import org.apache.flume.conf.MonitorDetails;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.marker.MonitorUses;
import org.mockito.Mockito;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

import java.time.Instant;

/**
 * Created by shays on 31/12/2017.
 */
public class MockMonitorInitiator {


    private static MonitorDetails monitorDetails;



    public static void setMockMonitor(Interceptor interceptor){
        PresidioExternalMonitoringService presidioExternalMonitoringService  = Mockito.mock(PresidioExternalMonitoringService.class);
        monitorDetails = new MonitorDetails(Instant.now(),presidioExternalMonitoringService,"mock-schema");

        if (interceptor instanceof MonitorUses){
            ((MonitorUses)interceptor).setMonitorDetails(monitorDetails);
        }

    }
}
