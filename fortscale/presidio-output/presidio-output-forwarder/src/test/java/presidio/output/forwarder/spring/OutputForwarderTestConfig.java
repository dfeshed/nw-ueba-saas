package presidio.output.forwarder.spring;


import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.forwarder.shell.OutputForwarderServiceCommands;

import java.util.Properties;

@Configuration
@Import({OutputForwarderTestConfigBeans.class, OutputForwarderBeans.class, ElasticsearchTestConfig.class,  BootShimConfig.class, OutputForwarderServiceCommands.class})
public class OutputForwarderTestConfig {

    @Bean
    public static TestPropertiesPlaceholderConfigurer testPropertiesPlaceholderConfigurer() {
        Properties properties = new Properties();
        properties.put("elasticsearch.clustername", EmbeddedElasticsearchInitialiser.EL_TEST_CLUSTER);
        properties.put("elasticsearch.host", "localhost");
        properties.put("elasticsearch.port", EmbeddedElasticsearchInitialiser.EL_TEST_PORT);
        properties.put("enable.metrics.export", false);
        properties.put("number.of.classifications", 23);
        properties.put("output.events.limit", 100);
        properties.put("output.events.page.size", 10);
        properties.put("indicators.contribution.limit.to.classification.percent", 0.3);
        properties.put("output.enriched.events.retention.in.days", 2);
        properties.put("indicators.store.page.size", 80);
        properties.put("events.store.page.size", 80);
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
