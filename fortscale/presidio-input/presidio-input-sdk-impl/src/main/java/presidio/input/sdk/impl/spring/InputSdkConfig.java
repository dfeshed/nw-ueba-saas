package presidio.input.sdk.impl.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.input.sdk.impl.repositories.DlpFileDataRepository;
import presidio.input.sdk.impl.services.DlpFileDataServiceImpl;
import presidio.input.sdk.impl.services.PresidioInputSdkMongoImpl;
import presidio.sdk.api.domain.DlpFileDataService;
import presidio.sdk.api.services.PresidioInputSdk;


@Configuration
public class InputSdkConfig {

    @Autowired
    private DlpFileDataRepository dlpFileDataRepository;

    @Bean
    private DlpFileDataService dlpFileDataService() {return new DlpFileDataServiceImpl(dlpFileDataRepository);}

    @Bean
    public PresidioInputSdk presidioInputSdk(){
        return  new PresidioInputSdkMongoImpl(dlpFileDataService());
    }

}
