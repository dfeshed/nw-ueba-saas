package presidio.ade.processes.shell;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.processes.shell.config.AccumulateSmartConfiguration;

import java.util.Properties;

@Configuration
@Import({MongodbTestConfig.class})
public class AccumulateSmartApplicationConfigurationTest extends AccumulateSmartConfiguration {
    @Bean
    public static TestPropertiesPlaceholderConfigurer accumulateSmartApplicationTestProperties() {
        Properties properties = new Properties();
        properties.put("smart.pageIterator.pageSize", 1000);
        properties.put("smart.pageIterator.maxGroupSize", 100);

        return new TestPropertiesPlaceholderConfigurer(properties);
    }

}