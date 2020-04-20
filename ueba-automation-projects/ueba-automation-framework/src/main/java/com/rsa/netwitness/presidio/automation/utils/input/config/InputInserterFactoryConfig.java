package com.rsa.netwitness.presidio.automation.utils.input.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.Authentication.InputAuthenticationEventsInserter;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.InputInserter;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.InputInserterFactory;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.activedirectory.InputActiveDirectoryEventsInserter;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.file.InputFileEventsInserter;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.process.InputProcessEventsInserter;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.registry.InputRegistryEventsInserter;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.util.List;

@Configuration
@Import(PresidioInputPersistencyServiceConfig.class)
public class InputInserterFactoryConfig {
    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Autowired
    private List<InputInserter> inputInserterList;

    @Bean
    public InputInserterFactory getInputInserterFactory(){
        return new InputInserterFactory(inputInserterList);
    }

    @Bean
    public InputAuthenticationEventsInserter getInputAuthenticationEventsInserter(){
        return new InputAuthenticationEventsInserter();
    }

    @Bean
    public InputActiveDirectoryEventsInserter getInputActiveDirectoryEventsInserter(){
        return new InputActiveDirectoryEventsInserter();
    }

    @Bean
    public InputFileEventsInserter getInputFileEventsInserter(){
        return new InputFileEventsInserter();
    }

    @Bean
    public InputProcessEventsInserter getInputProcessEventsInserter(){
        return new InputProcessEventsInserter();
    }

    @Bean
    public InputRegistryEventsInserter getInputRegistryEventsInserter(){
        return new InputRegistryEventsInserter();
    }

//    @Bean
//    public InputIOCEventsInserter getInputIOCEventsInserter(){
//        return new InputIOCEventsInserter();
//    }
}
