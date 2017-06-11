package presidio.input.core.spring;


import fortscale.services.config.ParametersValidationServiceConfig;
import fortscale.services.parameters.ParametersValidationService;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.input.core.services.api.InputExecutionService;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
@Import({MongoConfig.class, ParametersValidationServiceConfig.class, PresidioInputPersistencyServiceConfig.class, EnrichedDataStoreConfig.class})
public class InputCoreConfiguration {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Autowired
    private ParametersValidationService parametersValidationService;

    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Bean
    public InputExecutionService inputProcessService() {
        return new InputExecutionServiceImpl(parametersValidationService, presidioInputPersistencyService, enrichedDataStore);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new PresidioCommandLineRunner(inputProcessService());
    }


    // // TODO: 07-Jun-17 create generic command line runner and generic execution service for all our components
    private static class PresidioCommandLineRunner implements CommandLineRunner {

        private InputExecutionService inputExecutionService;

        public PresidioCommandLineRunner(InputExecutionService inputExecutionService) {
            this.inputExecutionService = inputExecutionService;
        }

        @Override
        public void run(String... params) throws Exception {
            this.inputExecutionService.run(params);

        }
    }
}
