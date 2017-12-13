package presidio.webapp.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({OutputWebappConfiguration.class, ElasticsearchTestConfig.class})
@Configuration
public class OutputWebappTestConfiguration {

}
