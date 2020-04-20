package com.rsa.netwitness.presidio.automation.utils.ade.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.RecordReaderFactoryServiceConfig;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserter;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserterFactory;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.activedirectory.AdeActiveDirectoryEventsInserter;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.authentication.AdeAuthenticationEventsInserter;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.dlpfile.AdeDLPFileEventsInserter;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.file.AdeFileEventsInserter;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.process.AdeProcessEventsInserter;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.registry.AdeRegistryEventsInserter;

import java.util.List;

/**
 * Created by YaronDL on 7/10/2017.
 */
@Configuration
@Import({AdeManagerSdkConfig.class, RecordReaderFactoryServiceConfig.class})
public class AdeInserterFactoryConfig {

    @Autowired
    private AdeManagerSdk adeManagerSDK;

    @Bean
    public AdeDLPFileEventsInserter getAdeDLPFileEventsInserter(){
        return new AdeDLPFileEventsInserter(adeManagerSDK);
    }

    @Bean
    public AdeFileEventsInserter getAdeFileEventsInserter(){
        return new AdeFileEventsInserter(adeManagerSDK);
    }

    @Bean
    public AdeActiveDirectoryEventsInserter getAdeActiveDirectoryEventsInserter(){
        return new AdeActiveDirectoryEventsInserter(adeManagerSDK);
    }

    @Bean
    public AdeAuthenticationEventsInserter getAdeAuthenticationEventsInserter(){
        return new AdeAuthenticationEventsInserter(adeManagerSDK);
    }

    @Bean
    public AdeProcessEventsInserter getAdeProcessEventsInserter(){
        return new AdeProcessEventsInserter(adeManagerSDK);
    }

    @Bean
    public AdeRegistryEventsInserter getAdeRegistryEventsInserter(){
        return new AdeRegistryEventsInserter(adeManagerSDK);
    }

    @Autowired
    private List<AdeInserter> adeInserterList;

    @Bean
    public AdeInserterFactory getAdeInserterFactory(){
        return new AdeInserterFactory(adeInserterList);
    }

}
