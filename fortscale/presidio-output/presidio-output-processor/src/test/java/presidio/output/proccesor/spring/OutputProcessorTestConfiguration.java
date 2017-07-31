package presidio.output.proccesor.spring;

import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.executions.common.ADEManagerSDK;
import presidio.ade.sdk.executions.online.ADEManagerSDKConfig;
import presidio.output.processor.OutputShellCommands;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.spring.AlertServiceElasticConfig;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({MongodbTestConfig.class,
        ADEManagerSDKConfig.class,
        AlertServiceElasticConfig.class,
        OutputShellCommands.class,
        BootShimConfig.class})
public class OutputProcessorTestConfiguration {

    @Autowired
    private ADEManagerSDK adeManagerSDK;

    @Autowired
    private AlertService alertService;

    @Bean
    public OutputExecutionService outputProcessService(){
        return new OutputExecutionServiceImpl(adeManagerSDK, alertService);
    }



}
