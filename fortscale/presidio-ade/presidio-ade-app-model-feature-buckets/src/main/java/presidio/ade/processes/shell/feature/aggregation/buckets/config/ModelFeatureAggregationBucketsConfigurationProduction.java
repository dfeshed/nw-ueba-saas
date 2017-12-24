package presidio.ade.processes.shell.feature.aggregation.buckets.config;

import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Configuration
@Import({MongoConfig.class,
        PresidioMonitoringConfiguration.class,
        ElasticsearchConfig.class})
public class ModelFeatureAggregationBucketsConfigurationProduction extends ModelFeatureAggregationBucketsConfiguration{
}
