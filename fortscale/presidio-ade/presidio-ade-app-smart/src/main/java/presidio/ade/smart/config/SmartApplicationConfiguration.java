package presidio.ade.smart.config;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.pagination.aggregated.AggregatedDataReader;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.ade.smart.SmartApplicationCommands;
import presidio.ade.smart.SmartScoringService;
import presidio.ade.smart.SmartService;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({
		// Mongo DB related configurations
		MongoDbBulkOpUtilConfig.class,
		// CLI commands related configurations
		SmartApplicationCommands.class,
		// Smart application related configurations
		SmartApplicationSmartRecordConfig.class,
		SmartApplicationAggregationRecordConfig.class,
		SmartApplicationAggregationDataReaderConfig.class,
		SmartApplicationSmartScoringServiceConfig.class,
		SmartApplicationSmartDataStoreConfig.class
})
public class SmartApplicationConfiguration {
	@Autowired
	private SmartRecordConfService smartRecordConfService;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregatedDataReader aggregatedDataReader;
	@Autowired
	private SmartScoringService smartScoringService;
	@Autowired
	private SmartDataStore smartDataStore;

	@Bean
	public SmartService smartService() {
		return new SmartService(
				smartRecordConfService,
				aggregatedFeatureEventsConfService,
				aggregatedDataReader,
				smartScoringService,
				smartDataStore);
	}
}
