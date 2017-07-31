package presidio.ade.processes.shell.config;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.creator.AggregationRecordsCreatorImpl;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.functions.AggrFeatureFuncServiceConfig;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Import({
//        application-specific confs
        AggregatedFeatureEventsConfServiceConfig.class,
//        common application confs
        AggrFeatureFuncServiceConfig.class
})
public class AggregationRecordsCreatorConfig {

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Bean
    public AggregationRecordsCreator aggregationRecordssCreator()
    {
        return new AggregationRecordsCreatorImpl(aggrFeatureEventFunctionsService,aggregatedFeatureEventsConfService);
    }
}