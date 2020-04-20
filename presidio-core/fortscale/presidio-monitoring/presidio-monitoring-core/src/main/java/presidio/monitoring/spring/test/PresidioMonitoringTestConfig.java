package presidio.monitoring.spring.test;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

/**
 * Created by barak_schuster on 12/3/17.
 */

@Configuration
@Import({PresidioMonitoringConfiguration.class, ElasticsearchTestConfig.class})
public class PresidioMonitoringTestConfig {

}