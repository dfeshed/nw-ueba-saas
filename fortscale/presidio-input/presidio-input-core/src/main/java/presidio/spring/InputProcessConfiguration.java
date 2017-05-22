package presidio.spring;

import presidio.services.api.InputProcessService;
import presidio.services.impl.InputProcessServiceImpl;
import fortscale.utils.mongodb.config.MongoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import(MongoConfig.class)
public class InputProcessConfiguration {

    @Bean
    public InputProcessService inputProcessService(){
        return new InputProcessServiceImpl();
    }


    @Bean
    public CommandLineRunner commandLineRunner() {

        return services -> inputProcessService().run(1,services);
    }

}
