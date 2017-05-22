package fortscale.spring;

import fortscale.services.InputProcessServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by shays on 21/05/2017.
 */
@Configuration
public class InputSdkImplConfig {
    @Bean
    InputProcessServiceImpl inputProcessService(){
        return  new InputProcessServiceImpl();
    }
}
