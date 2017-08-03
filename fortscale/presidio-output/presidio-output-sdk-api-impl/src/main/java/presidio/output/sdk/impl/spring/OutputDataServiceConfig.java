package presidio.output.sdk.impl.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.output.sdk.impl.services.OutputDataServiceImpl;

/**
 * Created by
 * efratn on 20/07/2017.
 */
@Configuration
@Import(EventPersistencyServiceConfig.class)
public class OutputDataServiceConfig {

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Bean
    private OutputDataServiceSDK outputDataServiceSDK() {
        return new OutputDataServiceImpl(eventPersistencyService);
    }



}
