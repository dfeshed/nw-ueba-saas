package presidio.output.processor.spring;

import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import presidio.ade.sdk.executions.common.ADEManagerSDK;
import presidio.ade.sdk.executions.online.ADEManagerSDKConfig;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.alert.AlertService;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({MongoConfig.class, ADEManagerSDKConfig.class, AlertServiceElasticConfig.class})
public class OutputProcessorConfiguration {

    @Autowired
    private ADEManagerSDK adeManagerSDK;

    @Autowired
    private AlertService alertService;

    @Bean
    public PresidioExecutionService outputProcessService(){
        return new OutputExecutionServiceImpl(adeManagerSDK, alertService);
    }



}
