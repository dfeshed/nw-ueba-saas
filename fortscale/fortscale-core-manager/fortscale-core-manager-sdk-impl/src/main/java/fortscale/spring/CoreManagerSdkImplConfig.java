package fortscale.spring;

import fortscale.spring.InputSdkImplConfig;
import fortscale.services.impl.CoreManagerServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import(InputSdkImplConfig.class)
public class CoreManagerSdkImplConfig {

    @Bean
    CoreManagerServiceImpl coreManagerService(){
        return new CoreManagerServiceImpl();
    }

}
