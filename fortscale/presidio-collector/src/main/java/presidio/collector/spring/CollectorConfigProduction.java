package presidio.collector.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import presidio.collector.services.api.CollectorExecutionService;

/**
 * Created by shays on 26/06/2017.
 */
@Import({MongoConfig.class, CollectorConfig.class})
public class CollectorConfigProduction {

    @Autowired
    private CollectorExecutionService collectorExecutionService;

    @Bean
    public CommandLineRunner commandLineRunner() {

        return params -> collectorExecutionService.run(params);
    }
}
