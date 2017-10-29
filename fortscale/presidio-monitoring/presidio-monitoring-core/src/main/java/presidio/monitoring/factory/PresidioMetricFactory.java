package presidio.monitoring.factory;

import org.springframework.util.ObjectUtils;
import presidio.monitoring.elastic.records.PresidioMetric;
import presidio.monitoring.elastic.records.PresidioMetricWithLogicTime;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class PresidioMetricFactory {

    public PresidioMetric creatingPresidioMetric(String metricName, Number metricValue, Set<String> tags, String unit, Instant logicTime) {
        return createMetric(metricName, metricValue, tags, unit, Date.from(logicTime));

    }

    public PresidioMetric creatingPresidioMetric(String metricName, Number metricValue, Set<String> tags, String unit) {
        return createMetric(metricName, metricValue, tags, unit, null);

    }

    public PresidioMetric creatingPresidioMetric(String metricName, Number metricValue, String unit) {
        Set<String> tags = new HashSet<>();
        return createMetric(metricName, metricValue, tags, unit, null);

    }

    private PresidioMetric createMetric(String metricName, Number metricValue, Set<String> tags, String unit, Date logicTime) {
        if (ObjectUtils.isEmpty(logicTime)) {
            return new PresidioMetric(metricName, metricValue.longValue(), tags, unit);
        } else {
            return new PresidioMetricWithLogicTime(metricName, metricValue.longValue(), tags, unit, logicTime);
        }
    }

}
