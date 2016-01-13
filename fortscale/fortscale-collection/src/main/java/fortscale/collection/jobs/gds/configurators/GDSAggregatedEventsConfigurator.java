package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;

import java.util.Map;

/**
 * @author gils
 * 12/01/2016
 */
public class GDSAggregatedEventsConfigurator extends GDSBaseConfigurator{
    @Override
    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {
        // 1. buckets definition (buckets.json)
        // 2. aggregated events (aggregated_feature_events.json)
        // 3. aggregated-feature_event-prevalance-stats.properties
    }

    @Override
    public void reset() throws Exception {

    }

    @Override
    public GDSConfigurationType getType() {
        return null;
    }
}
