package presidio.monitoring.aspect;

import org.aspectj.lang.Aspects;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import presidio.monitoring.aspect.metrics.PresidioCustomMetrics;

import javax.annotation.PostConstruct;

public class MonitroingAspectSetup {

    private MetricsEndpoint metricsEndpoint;
    private PresidioCustomMetrics presidioCustomMetrics;

    public MonitroingAspectSetup(MetricsEndpoint metricsEndpoint, PresidioCustomMetrics presidioCustomMetrics) {
        this.metricsEndpoint = metricsEndpoint;
        this.presidioCustomMetrics = presidioCustomMetrics;
    }

    @PostConstruct
    private void setupAspect() {
        Aspects.aspectOf(MonitoringAspects.class).setMetrics(this.metricsEndpoint, this.presidioCustomMetrics);
    }
}
