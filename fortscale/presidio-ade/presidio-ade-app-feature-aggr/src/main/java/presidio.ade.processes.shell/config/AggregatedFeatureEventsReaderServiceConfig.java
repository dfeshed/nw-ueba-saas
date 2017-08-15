package presidio.ade.processes.shell.config;

import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReaderConfig;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Configuration
@Import(        {
        AggregationEventsAccumulationDataReaderConfig.class,
        AggregatedFeatureEventsMongoStoreConfig.class
})
public class AggregatedFeatureEventsReaderServiceConfig {
    @Autowired
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
    @Autowired
    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;

    @Bean
    public AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService() {
        return new AggregatedFeatureEventsReaderService(aggregatedFeatureEventsMongoStore, aggregationEventsAccumulationDataReader);
    }
}
