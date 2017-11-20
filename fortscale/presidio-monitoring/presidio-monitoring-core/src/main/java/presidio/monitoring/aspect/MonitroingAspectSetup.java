package presidio.monitoring.aspect;

import org.aspectj.lang.Aspects;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;

import javax.annotation.PostConstruct;

public class MonitroingAspectSetup {

    private PresidioMetricEndPoint presidioMetricEndPoint;

    public MonitroingAspectSetup(PresidioMetricEndPoint presidioMetricEndPoint) {
        this.presidioMetricEndPoint = presidioMetricEndPoint;
    }

    @PostConstruct
    private void setupAspect() {
        Aspects.aspectOf(MonitoringAspects.class).setMetrics(this.presidioMetricEndPoint);
    }
}
