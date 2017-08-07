package fortscale.aggregation.feature.event.config;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 10/8/16.
 */
@Configuration
public class AggregatedFeatureEventsConfUtilServiceConfig {
    @Value("${streaming.event.field.type.aggr_event}")
    private String eventTypeFieldValue;
    @Value("${streaming.aggr_event.field.context}")
    private String contextFieldName;

    @Bean
    public AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService() {
        return new AggregatedFeatureEventsConfUtilService(eventTypeFieldValue, contextFieldName);
    }
}
