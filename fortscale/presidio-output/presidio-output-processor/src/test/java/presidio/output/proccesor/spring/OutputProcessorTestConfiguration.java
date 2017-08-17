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
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
