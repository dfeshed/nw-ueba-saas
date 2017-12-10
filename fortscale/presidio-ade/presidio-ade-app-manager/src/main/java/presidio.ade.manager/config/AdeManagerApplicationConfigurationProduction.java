package presidio.ade.manager.config;

import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

/**
 * Created by maria_dorohin on 7/26/17.
 */
@Configuration
@Import({
        PresidioMonitoringConfiguration.class,
        ElasticsearchConfig.class,
        MongoConfig.class
})
public class AdeManagerApplicationConfigurationProduction extends AdeManagerApplicationConfig {

}

