package presidio.ade.processes.shell;


import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;


@Configuration
@Import({
        PresidioMonitoringConfiguration.class,
        ElasticsearchConfig.class,
        MongoConfig.class,
})
public class FeatureAggregationsConfigurationProduction extends FeatureAggregationsConfiguration {

}
