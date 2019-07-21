package com.rsa.netwitness.presidio.automation.utils.input.config;

import fortscale.utils.mongodb.index.DynamicIndexingApplicationListenerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.sdk.impl.factory.PresidioInputPersistencyServiceFactory;
import com.rsa.netwitness.presidio.automation.utils.input.InputTestManager;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.InputInserterFactory;

@Configuration
@Import({PresidioInputPersistencyServiceFactory.class, InputInserterFactoryConfig.class,DynamicIndexingApplicationListenerConfig.class})
public class InputTestManagerConfig {
    @Autowired
    private PresidioInputPersistencyServiceFactory inputPersistencyServiceFactory;

    @Autowired
    private InputInserterFactory inputInserterFactory;

    @Bean
    public InputTestManager getInputTestManager(){
        return new InputTestManager(inputInserterFactory);
    }
}
