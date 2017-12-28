package presidio.ade.processes.shell.config;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.creator.AggregationRecordsCreatorImpl;
import fortscale.aggregation.creator.metrics.AggregationRecordsCreatorMetricsContainer;
import fortscale.aggregation.creator.metrics.AggregationRecordsCreatorMetricsContainerConfig;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.functions.AggrFeatureFuncServiceConfig;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@Import({
//        application-specific confs
        AggregatedFeatureEventsConfServiceConfig.class,
//        common application confs
        AggrFeatureFuncServiceConfig.class,
        AggregationRecordsCreatorMetricsContainerConfig.class
})
public class AggregationRecordsCreatorConfig {

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private AggregationRecordsCreatorMetricsContainer aggregationRecordsCreatorMetricsContainer;

    @Bean
    public AggregationRecordsCreator aggregationRecordssCreator() {
        return new AggregationRecordsCreatorImpl(aggrFeatureEventFunctionsService, aggregatedFeatureEventsConfService,aggregationRecordsCreatorMetricsContainer);
    }
}