package fortscale.aggregation.feature.event.store.config;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationService;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationServiceConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Import({
        AggregatedFeatureNameTranslationServiceConfig.class})
public class AggregatedFeatureEventsMongoStoreConfig {
    @Value("#{'${fortscale.store.collection.backup.prefix}'.split(',')}")
    private List<String> backupCollectionNamesPrefixes;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService;

    @Bean
    public AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore() {
        return new AggregatedFeatureEventsMongoStore(
                mongoTemplate,
                aggregatedFeatureEventsConfService,
                statsService,
                aggregatedFeatureNameTranslationService,
                backupCollectionNamesPrefixes);
    }
}
