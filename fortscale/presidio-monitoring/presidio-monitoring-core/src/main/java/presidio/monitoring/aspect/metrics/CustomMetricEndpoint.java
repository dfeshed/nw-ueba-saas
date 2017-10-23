package presidio.monitoring.aspect.metrics;

import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import presidio.monitoring.elastic.records.PresidioMetric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "endpoints.metrics")
public class CustomMetricEndpoint extends MetricsEndpoint {

    private static final Logger logger = Logger.getLogger(CustomMetricEndpoint.class);

    private final List<PublicMetrics> publicMetrics;

    private final String UNIT_TYPE_LONG = "long";

    private boolean isFlush;

    /**
     * Create a new {@link CustomMetricEndpoint} instance.
     *
     * @param publicMetrics the metrics to expose
     */
    public CustomMetricEndpoint(PublicMetrics publicMetrics) {
        this(Collections.singleton(publicMetrics));
    }

    /**
     * Create a new {@link MetricsEndpoint} instance.
     *
     * @param publicMetrics the metrics to expose. The collection will be sorted using the
     *                      {@link AnnotationAwareOrderComparator}.
     */
    public CustomMetricEndpoint(Collection<PublicMetrics> publicMetrics) {
        super(publicMetrics);
        Assert.notNull(publicMetrics, "PublicMetrics must not be null");
        this.publicMetrics = new ArrayList<PublicMetrics>(publicMetrics);
        AnnotationAwareOrderComparator.sort(this.publicMetrics);
        this.isFlush = false;
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
                if (publicMetric instanceof PresidioCustomMetrics) {
                    for (PresidioMetric metric : ((PresidioCustomMetrics) publicMetric).applicationMetrics(this.isFlush)) {
                        result.put(metric.getName(), metric);
                    }
                } else {
                    for (Metric<?> metric : publicMetric.metrics()) {
                        result.put(metric.getName(), new PresidioMetric(metric.getName(), getLong(metric.getValue()), new HashSet(), UNIT_TYPE_LONG));
                    }
                }
            } catch (Exception ex) {
                logger.info("Error happened when trying to get all metrics. ", ex);
            }
        }
        return result;
    }


    private <T extends Number> long getLong(T value) {
        Long longNumber;
        if (value instanceof Double) {
            longNumber = ((Double) value).longValue();
        } else if (value instanceof Integer) {
            longNumber = ((Integer) value).longValue();
        } else {
            longNumber = (Long) value;
        }
        return longNumber.longValue();
    }

    public void setFlush(boolean flush) {
        isFlush = flush;
    }
}