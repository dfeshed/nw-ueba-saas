package presidio.ade.processes.shell.scoring.aggregation.config.application;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.common.shell.config.ShellCommonCommandsConfig;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.shell.BootShimConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.aggr.AggregatedDataStoreConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.processes.shell.ScoreAggregationsExecutionServiceImpl;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsBucketService;
import presidio.ade.processes.shell.scoring.aggregation.config.services.AggregationRecordsCreatorConfig;
import presidio.ade.processes.shell.scoring.aggregation.config.services.EnrichedEventsScoringServiceConfig;
import presidio.ade.processes.shell.scoring.aggregation.config.services.ScoreAggregationsBucketServiceConfiguration;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({
//        application-specific confs
        EnrichedEventsScoringServiceConfig.class,
        AggregationRecordsCreatorConfig.class,
        ScoreAggregationsBucketServiceConfiguration.class,
//        common application confs
        EnrichedDataStoreConfig.class,
        AggregatedDataStoreConfig.class,
        ShellCommonCommandsConfig.class,
        BootShimConfig.class,
        NullStatsServiceConfig.class, // todo: remove this
})
public class ScoreAggregationsApplicationConfig {
    @Autowired
    private EnrichedEventsScoringService enrichedEventsScoringService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private ScoreAggregationsBucketService scoreAggregationsBucketService;
    @Autowired
    private AggregationRecordsCreator aggregationRecordsCreator;
    @Autowired
    private AggregatedDataStore aggregatedDataStore;

    @Bean
    public PresidioExecutionService presidioExecutionService() {
        return new ScoreAggregationsExecutionServiceImpl(
                enrichedEventsScoringService, enrichedDataStore, scoreAggregationsBucketService, aggregationRecordsCreator, aggregatedDataStore);
    }
}
