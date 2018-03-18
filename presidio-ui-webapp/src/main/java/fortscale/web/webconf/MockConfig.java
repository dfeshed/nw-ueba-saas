package fortscale.web.webconf;

import fortscale.domain.core.dao.FavoriteUserFilterRepository;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.web.demoservices.DemoBuilder;
import fortscale.web.demoservices.services.MockDemoAlertsServiceImpl;
import fortscale.web.demoservices.services.MockDemoEvidencesServiceImpl;
import fortscale.web.demoservices.services.MockDemoUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by shays on 26/07/2017.
 */
@Configuration
@Profile("mock")
public class MockConfig {

    @Autowired
    private FavoriteUserFilterRepository favoriteUserFilterRepository;

    @Autowired
    private DemoBuilder demoBuilder;

    @Bean
    DemoBuilder demoBuilder(){
        return new DemoBuilder();
    }

    @Bean(name = "alertsService")
    AlertsService alertsService(){
        return new MockDemoAlertsServiceImpl(userService(),demoBuilder);
    }

    @Bean(name = "evidencesService")
    EvidencesService evidencesService(){
        return new MockDemoEvidencesServiceImpl(demoBuilder);
    }


    @Bean(name = "userService")
    UserService userService(){
        return new MockDemoUserServiceImpl(favoriteUserFilterRepository, demoBuilder);
    }

}
