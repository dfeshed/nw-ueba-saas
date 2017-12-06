package presidio.monitoring.aspect;

import org.aspectj.lang.Aspects;
import presidio.monitoring.endPoint.PresidioMetricBucket;

import javax.annotation.PostConstruct;

public class MonitroingAspectSetup {

    private PresidioMetricBucket presidioMetricBucket;

    public MonitroingAspectSetup(PresidioMetricBucket presidioMetricBucket) {
        this.presidioMetricBucket = presidioMetricBucket;
    }

    @PostConstruct
    private void setupAspect() {
        Aspects.aspectOf(MonitoringAspects.class).setMetrics(this.presidioMetricBucket);
    }
}
