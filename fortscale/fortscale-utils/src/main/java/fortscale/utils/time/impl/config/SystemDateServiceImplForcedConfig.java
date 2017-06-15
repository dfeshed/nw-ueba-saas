package fortscale.utils.time.impl.config;

import fortscale.utils.time.impl.SystemDateServiceImpl;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * SystemDateService configuration class for the standard implementation for testing. The service will be forced
 * to the specified time by 'fortscale.system.date.service.force.initialDate'. The default is '2016-05-15T08:00:00Z'
 *
 * See also SystemDateService
 *
 * Created by gaashh on 9/11/16.
 */

@Configuration
public class SystemDateServiceImplForcedConfig {

    // Forced initial date
    @Value("#{ T(java.time.Instant).parse('${fortscale.system.date.service.force.initialDate:2015-05-15T08:00:00Z}') }")
    private Instant forcedInitialDate;

    /**
     *
     * The main bean function, create the system date service for the standard implementation
     *
     * @return new instance of SystemDateServiceImpl()
     */
    @Bean
    public SystemDateServiceImpl SystemDateServiceImpl() {

        SystemDateServiceImpl dateService = new SystemDateServiceImpl();
        dateService.forceInstant(forcedInitialDate);

        return dateService;
    }

}



