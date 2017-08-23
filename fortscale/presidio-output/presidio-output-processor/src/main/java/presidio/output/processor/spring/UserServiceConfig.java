package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.services.user.UserService;
import presidio.output.processor.services.user.UserServiceImpl;

/**
 * Created by efratn on 22/08/2017.
 */
@Configuration
@Import({EventPersistencyServiceConfig.class})
public class UserServiceConfig {

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Bean
    public UserService userService() {
        return new UserServiceImpl(eventPersistencyService, userPersistencyService);
    }

}
