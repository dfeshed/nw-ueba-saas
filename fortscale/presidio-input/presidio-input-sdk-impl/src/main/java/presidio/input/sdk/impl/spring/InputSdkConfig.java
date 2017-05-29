package presidio.input.sdk.impl.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.input.sdk.impl.repositories.DlpFileDataRepository;
import presidio.input.sdk.impl.services.DlpFileDataServiceImpl;
import presidio.input.sdk.impl.services.PresidioInputPersistencyServiceMongoImpl;
import presidio.sdk.api.domain.DlpFileDataService;
import presidio.sdk.api.services.PresidioInputPersistencyService;


@Configuration
public class InputSdkConfig {

    @Autowired
    private DlpFileDataRepository dlpFileDataRepository;

    @Bean
    public DlpFileDataService dlpFileDataService() {
        return new DlpFileDataServiceImpl(dlpFileDataRepository);
    }

    @Bean
    public PresidioInputPersistencyService presidioInputSdk() {
        return new PresidioInputPersistencyServiceMongoImpl(dlpFileDataService());
    }

}
