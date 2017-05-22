package presidio.input.core.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.input.core.services.InputProcessServiceImpl;

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
