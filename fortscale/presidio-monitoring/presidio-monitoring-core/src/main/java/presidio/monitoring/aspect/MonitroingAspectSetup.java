package presidio.monitoring.aspect;

import org.aspectj.lang.Aspects;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;
import presidio.monitoring.factory.PresidioMetricFactory;

import javax.annotation.PostConstruct;

public class MonitroingAspectSetup {

    private PresidioMetricEndPoint presidioMetricEndPoint;
    private PresidioMetricFactory presidioMetricFactory;

    public MonitroingAspectSetup(PresidioMetricEndPoint presidioMetricEndPoint, PresidioMetricFactory presidioMetricFactory) {
        this.presidioMetricEndPoint = presidioMetricEndPoint;
        this.presidioMetricFactory = presidioMetricFactory;
    }

    @PostConstruct
    private void setupAspect() {
        Aspects.aspectOf(MonitoringAspects.class).setMetrics(this.presidioMetricEndPoint, this.presidioMetricFactory);
    }
}
