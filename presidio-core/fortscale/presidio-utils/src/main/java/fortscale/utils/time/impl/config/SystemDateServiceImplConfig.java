package fortscale.utils.time.impl.config;

import fortscale.utils.time.impl.SystemDateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * SystemDateService configuration class for the standard implementation.
 *
 * See also SystemDateService
 *
 * Created by gaashh on 7/10/16.
 */


@Configuration
public class SystemDateServiceImplConfig {


    /**
     *
     * The main bean function, create the system date service for the standard implementation
     *
     * @return new instance of SystemDateServiceImpl()
     */
    @Bean
    public SystemDateServiceImpl SystemDateServiceImpl() {

        SystemDateServiceImpl dateService = new SystemDateServiceImpl();

        return dateService;
    }

}



