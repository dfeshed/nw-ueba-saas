package presidio.adapter.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import presidio.adapter.services.api.AdapterExecutionService;

/**
 * Created by shays on 26/06/2017.
 */
@Import({MongoConfig.class, AdapterConfig.class})
public class AdapterConfigProduction {

    @Autowired
    private AdapterExecutionService adapterExecutionService;

    @Bean
    public CommandLineRunner commandLineRunner() {

        return params -> adapterExecutionService.run(params);
    }
}
