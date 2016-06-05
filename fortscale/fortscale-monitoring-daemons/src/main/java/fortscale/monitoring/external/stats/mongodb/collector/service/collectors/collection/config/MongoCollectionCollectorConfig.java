package fortscale.monitoring.external.stats.mongodb.collector.service.collectors.collection.config;

import fortscale.monitoring.external.stats.mongodb.collector.service.collectors.collection.MongoCollectionCollector;
import fortscale.monitoring.external.stats.mongodb.collector.service.collectors.config.MongodbConfig;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
@Import(MongodbConfig.class)
public class MongoCollectionCollectorConfig {

    @Autowired
    StatsService statsService;
    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    MongoCollectionCollector mongoCollectionCollector()
    {
        return new MongoCollectionCollector(mongoTemplate,statsService);
    }
}
