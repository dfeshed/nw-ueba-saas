package presidio.webapp.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({OutputWebappConfiguration.class, ElasticsearchConfig.class})
@Configuration
public class OutputWebappProductionConfiguration {


}
