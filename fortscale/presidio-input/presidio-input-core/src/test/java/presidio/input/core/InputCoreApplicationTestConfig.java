package presidio.input.core;

import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.core.spring.AdeDataServiceConfig;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

/**
 * Created by efratn on 26/06/2017.
 */
@Configuration
@Import({PresidioInputPersistencyServiceConfig.class, AdeDataServiceConfig.class})
public class InputCoreApplicationTestConfig {

        @Autowired
        private PresidioInputPersistencyService presidioInputPersistencyService;

        @Autowired
        private AdeDataService adeDataService;

        @Bean
        public PresidioExecutionService inputExecutionService() {
            return new InputExecutionServiceImpl(presidioInputPersistencyService, adeDataService);
        }

}
