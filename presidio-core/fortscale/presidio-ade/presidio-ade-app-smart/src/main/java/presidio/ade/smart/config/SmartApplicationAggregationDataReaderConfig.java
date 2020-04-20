package presidio.ade.smart.config;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.pagination.aggregated.AggregatedDataReader;
import presidio.ade.domain.pagination.aggregated.AggregatedRecordPaginationService;
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggregatedDataStoreMongoImpl;

/**
 * @author Lior Govrin
 */
@Configuration
public class SmartApplicationAggregationDataReaderConfig {
	@Value("${presidio.ade.aggregation.data.pagination.service.num.of.context.ids.in.page}")
	private int numOfContextIdsInPage;

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbBulkOpUtil mongoDbBulkOpUtil;

	@Bean
	public AggregatedDataReader aggregatedDataReader() {
		AggregatedDataReader aggregatedDataReader = new AggregatedDataStoreMongoImpl(
				mongoTemplate, new AggrDataToCollectionNameTranslator(), mongoDbBulkOpUtil);
		aggregatedDataReader.setAggregatedRecordPaginationService(new AggregatedRecordPaginationService(
				numOfContextIdsInPage, aggregatedDataReader));
		return aggregatedDataReader;
	}
}
