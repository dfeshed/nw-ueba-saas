package presidio.input.sdk.impl.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.input.sdk.impl.services.PresidioInputSdkMongoImpl;


@Configuration
public class InputSdkConfig {
    @Bean
    PresidioInputSdkMongoImpl inputProcessService() {
        return new PresidioInputSdkMongoImpl();
    }
}
