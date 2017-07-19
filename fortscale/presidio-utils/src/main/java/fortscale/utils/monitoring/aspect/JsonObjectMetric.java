package fortscale.utils.monitoring.aspect;

import org.springframework.boot.actuate.metrics.Metric;

import java.util.Set;

public class JsonObjectMetric extends Metric{

    private Set tags;
    public JsonObjectMetric(String name, Number value) {
        super(name, value);
    }

    public JsonObjectMetric(String name, Number value, Set tags) {
        super(name, value);
        this.tags=tags;
    }
}
