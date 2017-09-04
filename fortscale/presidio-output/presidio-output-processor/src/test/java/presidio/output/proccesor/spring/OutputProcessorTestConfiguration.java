package presidio.output.proccesor.spring;

import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.OutputShellCommands;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserScoreService;
import presidio.output.processor.services.user.UserService;
import presidio.output.processor.spring.AlertServiceElasticConfig;
import presidio.output.processor.spring.UserServiceConfig;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({MongodbTestConfig.class,
        AdeManagerSdkConfig.class,
        AlertServiceElasticConfig.class,
        OutputShellCommands.class,
        BootShimConfig.class,
        UserServiceConfig.class,
        EventPersistencyServiceConfig.class})
public class OutputProcessorTestConfiguration {

    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserService userService;

    @Value("${smart.threshold.score}")
    private int smartThreshold;

    @Value("${smart.page.size}")
    private int smartPageSize;

    @Autowired
    private UserScoreService userScoreService;

    @Bean
    public OutputExecutionService outputProcessService() {
        return new OutputExecutionServiceImpl(adeManagerSdk, alertService, userService, userScoreService, smartThreshold, smartPageSize);
    }
}
