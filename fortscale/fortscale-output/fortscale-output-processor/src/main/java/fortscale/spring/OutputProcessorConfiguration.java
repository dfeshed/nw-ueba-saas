package fortscale.spring;

import fortscale.services.OutputProcessServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
public class OutputProcessorConfiguration {

    @Bean
    public OutputProcessServiceImpl outputProcessService(){
        return new OutputProcessServiceImpl();
    }

    @Bean
    public CommandLineRunner commandLineRunner2() {

        return params -> outputProcessService().run(params);
    }


}
