package presidio.output.sdk.impl.spring;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.output.sdk.impl.services.OutputDataServiceImpl;

/**
 * Created by
 * efratn on 20/07/2017.
 */
@Configuration
public class OutputDataServiceConfig {

    @Bean
    private OutputDataServiceSDK outputDataServiceSDK() {
        return new OutputDataServiceImpl();
    }



}
