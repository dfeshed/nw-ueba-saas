package presidio.output.processor.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.services.user.UserPropertiesUpdateService;
import presidio.output.processor.services.user.UserPropertiesUpdateServiceImpl;

@Configuration
public class UserPropertiesUpdateServiceConfiguration {

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Bean
    private UserPropertiesUpdateService userPropertiesUpdateService() {
        return new UserPropertiesUpdateServiceImpl(eventPersistencyService);
    }
}
