package fortscale.web.webconf;

import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.ad.AdTaskPersistencyService;
import fortscale.services.ad.AdTaskPersistencyServiceImpl;
import fortscale.web.services.TaskService;
import fortscale.web.services.AdTaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdTaskConfig {

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    private ActiveDirectoryService activeDirectoryService;

    @Bean
    public AdTaskPersistencyService adTaskPersistencyService(){
        return new AdTaskPersistencyServiceImpl(applicationConfigurationService);
    }

    @Bean
    public TaskService adTaskService(){
        return new AdTaskServiceImpl(activeDirectoryService, adTaskPersistencyService());
    }
}
