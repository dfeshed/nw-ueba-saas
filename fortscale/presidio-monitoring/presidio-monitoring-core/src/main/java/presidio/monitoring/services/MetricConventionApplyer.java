package presidio.monitoring.services;

import presidio.monitoring.sdk.api.services.model.Metric;

/**
 *
 * Created by efratn on 21/11/2017.
 */
public interface MetricConventionApplyer {

    void apply(Metric metric);
}

