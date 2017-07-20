package presidio.monitoring.aspect;

import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "endpoints.metrics")
public class CustomMetricEndpoint extends MetricsEndpoint {

    private final List<PublicMetrics> publicMetrics;

    /**
     * Create a new {@link CustomMetricEndpoint} instance.
     * @param publicMetrics the metrics to expose
     */
    public CustomMetricEndpoint(PublicMetrics publicMetrics) {
        this(Collections.singleton(publicMetrics));
    }

    /**
     * Create a new {@link MetricsEndpoint} instance.
     * @param publicMetrics the metrics to expose. The collection will be sorted using the
     * {@link AnnotationAwareOrderComparator}.
     */
    public CustomMetricEndpoint(Collection<PublicMetrics> publicMetrics) {
        super(publicMetrics);
        Assert.notNull(publicMetrics, "PublicMetrics must not be null");
        this.publicMetrics = new ArrayList<PublicMetrics>(publicMetrics);
        AnnotationAwareOrderComparator.sort(this.publicMetrics);
    }

    public void registerPublicMetrics(PublicMetrics metrics) {
        this.publicMetrics.add(metrics);
        AnnotationAwareOrderComparator.sort(this.publicMetrics);
    }

    public void unregisterPublicMetrics(PublicMetrics metrics) {
        this.publicMetrics.remove(metrics);
    }

    @Override
    public Map<String, Object> invoke() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        List<PublicMetrics> metrics = new ArrayList<PublicMetrics>(this.publicMetrics);
        for (PublicMetrics publicMetric : metrics) {
            try {
                if(publicMetric instanceof CustomMetric){
                    for (JsonObjectMetric<?> metric : ((CustomMetric) publicMetric).customMetrics()) {
                        result.put(metric.getName(), metric.getObject());
                    }
                }
                else {
                    for (Metric<?> metric : publicMetric.metrics()) {
                        result.put(metric.getName(), metric.getValue());
                    }
                }
            }
            catch (Exception ex) {
                // Could not evaluate metrics
            }
        }
        return result;
    }

}