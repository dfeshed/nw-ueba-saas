package presidio.ade.processes.shell.feature.aggregation.buckets.config;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Configuration
@Import({MongoConfig.class})
public class ModelFeatureAggregationBucketsConfigurationProduction extends ModelFeatureAggregationBucketsConfiguration{
}
