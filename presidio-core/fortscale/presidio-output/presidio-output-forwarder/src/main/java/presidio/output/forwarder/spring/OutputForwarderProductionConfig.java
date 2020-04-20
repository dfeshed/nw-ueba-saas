package presidio.output.forwarder.spring;


import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({OutputForwarderConfigBeans.class, OutputForwarderBeans.class, ElasticsearchConfig.class})
public class OutputForwarderProductionConfig {

}
