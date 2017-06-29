package fortscale.ml.scorer.enriched_events;

import fortscale.ml.model.config.ModelBuildingConfiguration;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.spring.config.ScoringSpringConfiguration;
import fortscale.utils.recordreader.RecordReaderFactory;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.recordreader.transformation.EpochtimeTransformation;
import fortscale.utils.recordreader.transformation.Transformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.AdeRecordReaderFactory;
import presidio.ade.domain.record.scored.AdeScoredRecordReaderFactory;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStoreMongoConfig;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Import({
        ModelBuildingConfiguration.class,
        ScoringSpringConfiguration.class,
        ScoredEnrichedDataStoreMongoConfig.class,
        AdeEnrichedScoredRecordBuilderConfig.class
})
public class EnrichedEventsScoringServiceConfig {
    @Autowired
    private Collection<RecordReaderFactory> recordReaderFactories;
    @Autowired
    private Collection<Transformation<?>> transformations;
    @Autowired
    private Map<String, Transformation<?>> featureNameToTransformationMap;
    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;
    @Autowired
    private ScoringService scoringService;
    @Autowired
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    @Autowired
    private AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder;

    @Bean
    public Map<String, Transformation<?>> featureNameToTransformationMap() {
        return transformations.stream().collect(Collectors.toMap(Transformation::getFeatureName, Function.identity()));
    }

    @Bean
    public RecordReaderFactoryService recordReaderFactoryService() {
        return new RecordReaderFactoryService(recordReaderFactories, featureNameToTransformationMap);
    }

    @Bean
    public EnrichedEventsScoringService enrichedEventsScoringService() {
        return new EnrichedEventsScoringServiceImpl(
                recordReaderFactoryService,
                scoringService,
                scoredEnrichedDataStore,
                adeEnrichedScoredRecordBuilder);
    }

    // TODO: Configure here all relevant record reader factories

    @Bean
    public AdeRecordReaderFactory adeRecordReaderFactory() {
        return new AdeRecordReaderFactory();
    }

    @Bean
    public AdeScoredRecordReaderFactory adeScoredRecordReaderFactory() {
        return new AdeScoredRecordReaderFactory();
    }

    // TODO: Configure here all relevant transformations

    @Bean
    public EpochtimeTransformation epochtimeTransformation() {
        return new EpochtimeTransformation("two_minute_resolution_epochtime", "date_time", 120);
    }
}
