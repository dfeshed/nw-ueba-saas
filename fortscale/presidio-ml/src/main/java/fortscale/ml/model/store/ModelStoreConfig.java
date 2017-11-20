package fortscale.ml.model.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Duration;

@Configuration
public class ModelStoreConfig {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Value("#{T(java.time.Duration).parse('${presidio.ttl.oldest.allowed.model:P100D}')}")
	private Duration ttlOldestAllowedModel;
	@Value("${presidio.model.store.aggregation.pagination.size:10000}")
	private int modelAggregationPaginationSize;
	@Value("${presidio.model.store.query.pagination.size:200000}")
	private int modelQueryPaginationSize;

	@Bean
	public ModelStore modelStore() {
		return new ModelStore(mongoTemplate, ttlOldestAllowedModel, modelAggregationPaginationSize, modelQueryPaginationSize);
	}
}
