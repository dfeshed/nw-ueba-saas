package presidio.monitoring.services;

import presidio.monitoring.records.Metric;

/**
 *
 * Created by efratn on 21/11/2017.
 */
public interface MetricConventionApplyer {

    void apply(Metric metric);
}

