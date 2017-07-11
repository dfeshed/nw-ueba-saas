package fortscale.ml.processes.shell;

import fortscale.aggregation.creator.AggregationsCreator;
import fortscale.aggregation.creator.AggregationsCreatorConfig;
import fortscale.common.general.DataSource;
import fortscale.ml.model.cache.EventModelsCacheServiceConfig;
import fortscale.ml.processes.shell.scoring.aggregation.ScoreAggregationsBucketService;
import fortscale.ml.processes.shell.scoring.aggregation.ScoreAggregationsBucketServiceConfiguration;
import fortscale.ml.processes.shell.scoring.aggregation.ScoreAggregationsService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringServiceConfig;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.mongodb.config.MongoConfig;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import presidio.ade.domain.store.aggr.AggrDataStore;
import presidio.ade.domain.store.aggr.AggrDataStoreConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;

import java.time.Instant;

/**
 * Created by barak_schuster on 6/14/17.
 */
@Configuration
@Import({EnrichedDataStoreConfig.class,
        MongoConfig.class,
        EnrichedEventsScoringServiceConfig.class,
        ScoreAggregationsBucketServiceConfiguration.class,
        EventModelsCacheServiceConfig.class,
        AggregationsCreatorConfig.class,
        AggrDataStoreConfig.class,
        NullStatsServiceConfig.class,// todo: remove this
})
@EnableSpringConfigured
public class ScoreAggregationServiceConfiguration {

    @Autowired
    private EnrichedEventsScoringService enrichedEventsScoringService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private ScoreAggregationsBucketService scoreAggregationsBucketService;
    @Autowired
    private AggregationsCreator aggregationsCreator;
    @Autowired
    private AggrDataStore aggrDataStore;

    @Bean
    public CommandLineRunner commandLineRunner() {

        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                // todo: all of this will be change when using spring shell
                String dataSourceParam = DataSource.DLPFILE.getName();
                Instant startTimeParam = Instant.parse("temp");
                Instant endTimeParam = Instant.parse("temp");
                FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds((long)Float.parseFloat("36000"));
                ScoreAggregationsService scoreAggregationsService = new ScoreAggregationsService(fixedDurationStrategy, enrichedDataStore,enrichedEventsScoringService, scoreAggregationsBucketService, aggregationsCreator,aggrDataStore);
                TimeRange timeRange = new TimeRange(startTimeParam, endTimeParam);
                scoreAggregationsService.execute(timeRange,dataSourceParam);
            }
        };
    }


}
