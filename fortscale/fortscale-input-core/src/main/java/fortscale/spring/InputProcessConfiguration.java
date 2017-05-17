package fortscale.spring;

import fortscale.services.InputProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
public class InputProcessConfiguration {


    @Bean
    public CommandLineRunner commandLineRunner2() {
        InputProcessService processService = new InputProcessService();
        return services -> processService.run(2,services);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        InputProcessService processService = new InputProcessService();
        return services -> processService.run(1,services);
    }

}
