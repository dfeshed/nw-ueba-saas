package presidio.ade.modeling.config;

import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({ModelingServiceConfiguration.class, MongoConfig.class,PresidioMonitoringConfiguration.class,
        ElasticsearchConfig.class})
public class ModelingServiceConfigurationProduction {}
