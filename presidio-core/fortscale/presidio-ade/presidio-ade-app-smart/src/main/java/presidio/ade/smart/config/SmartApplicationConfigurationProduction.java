package presidio.ade.smart.config;

import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({PresidioMonitoringConfiguration.class,
        ElasticsearchConfig.class,
        SmartApplicationConfiguration.class,
        MongoConfig.class})
public class SmartApplicationConfigurationProduction {}
