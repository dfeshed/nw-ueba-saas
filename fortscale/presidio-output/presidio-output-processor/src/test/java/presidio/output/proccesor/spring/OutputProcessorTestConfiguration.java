package presidio.output.proccesor.spring;

import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.output.processor.OutputShellCommands;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.spring.AlertServiceElasticConfig;

import java.util.Properties;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({MongodbTestConfig.class,
        AdeManagerSdkConfig.class,
        AlertServiceElasticConfig.class,
        OutputShellCommands.class,
        BootShimConfig.class})
public class OutputProcessorTestConfiguration {

    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Autowired
    private AlertService alertService;

    @Bean
    public OutputExecutionService outputProcessService() {
        return new OutputExecutionServiceImpl(adeManagerSdk, alertService);
    }
    @Bean
    public static TestPropertiesPlaceholderConfigurer outputProcessorTestConfigurationTestConfigurer()
    {
        Properties properties = new Properties();
        properties.put("streaming.event.field.type.aggr_event", "aggr_event");
        properties.put("streaming.aggr_event.field.context", "context");
        properties.put("fortscale.ademanager.aggregation.feature.event.conf.json.file.name","classpath:config/asl/manager/aggregated-features/*/*.json");
        properties.put("fortscale.ademanager.aggregation.bucket.conf.json.file.name","classpath:config/asl/manager/feature-buckets/*/*.json");
        properties.put("severity.critical", 95);
        properties.put("severity.high", 85);
        properties.put("severity.mid", 70);
        properties.put("severity.low", 50);
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
