package presidio.input.sdk.impl.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import presidio.input.sdk.impl.repositories.DlpFileDataRepository;
import presidio.input.sdk.impl.services.DlpFileDataServiceImpl;
import presidio.input.sdk.impl.services.PresidioInputPersistencyServiceMongoImpl;
import presidio.sdk.api.domain.DlpFileDataService;
import presidio.sdk.api.services.PresidioInputPersistencyService;


@Configuration
@EnableMongoRepositories(basePackageClasses = DlpFileDataRepository.class)
public class PresidioInputPersistencyServiceConfig {

    @Autowired
    private DlpFileDataRepository dlpFileDataRepository;

    @Bean
    public DlpFileDataService dlpFileDataService() {
        return new DlpFileDataServiceImpl(dlpFileDataRepository);
    }

    @Bean
    public PresidioInputPersistencyService presidioInputPersistencyService() {
        return new PresidioInputPersistencyServiceMongoImpl(dlpFileDataService());
    }

}
