package presidio.collector.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.collector.services.api.ReaderService;
import presidio.collector.services.impl.ReaderServiceImpl;
import presidio.sdk.impl.spring.CoreManagerSdkImplConfig;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({CoreManagerSdkImplConfig.class, MongoConfig.class})
public class CollectorConfig {

    @Bean
    ReaderService  readerService(){
        return new ReaderServiceImpl();
    }

    @Bean
    public CommandLineRunner commandLineRunner() {

        return params -> readerService().run(params);
    }



}
