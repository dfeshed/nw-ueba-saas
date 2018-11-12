package presidio.output.forwarder.shell;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.forwarder.spring.OutputForwarderBeans;
import presidio.output.forwarder.spring.OutputForwarderConfigBeans;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;
import presidio.output.forwarder.strategy.plugins.aws.SqsForwarderStrategy;

import java.util.Properties;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SqsOutputForwarderSpringContextTest.sqsOutputForwarderSpringConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SqsOutputForwarderSpringContextTest {

    @Autowired
    OutputForwarderExecutionService outputForwarderExecutionService;

    @Autowired
    ForwarderStrategyFactory forwarderStrategyFactory;

    @Test
    public void contextLoads() {
        Assert.assertNotNull(outputForwarderExecutionService);
        // verify that sqs strategy was loaded successfully
        Assert.assertEquals(forwarderStrategyFactory.getStrategy("sqs").getClass(),
                SqsForwarderStrategy.class);

    }

    @Configuration
    @Import({OutputForwarderBeans.class, ElasticsearchTestConfig.class,
            BootShimConfig.class, OutputForwarderServiceCommands.class,
            OutputForwarderConfigBeans.class, OutputForwarderBeans.class})
    public static class sqsOutputForwarderSpringConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer sqstestPropertiesPlaceholderConfigurer() {
            Properties properties = new Properties();
            properties.put("elasticsearch.clustername", EmbeddedElasticsearchInitialiser.EL_TEST_CLUSTER);
            properties.put("elasticsearch.host", "localhost");
            properties.put("elasticsearch.port", EmbeddedElasticsearchInitialiser.EL_TEST_PORT);
            properties.put("enable.metrics.export", false);
            properties.put("number.of.classifications", 28);
            properties.put("output.events.limit", 100);
            properties.put("output.events.page.size", 10);
            properties.put("indicators.contribution.limit.to.classification.percent", 0.3);
            properties.put("output.enriched.events.retention.in.days", 2);
            properties.put("indicators.store.page.size", 80);
            properties.put("events.store.page.size", 80);
            // sqs startegy choosing property:
            properties.put("presidio.output.forwarder.strategy.name", "sqs");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
