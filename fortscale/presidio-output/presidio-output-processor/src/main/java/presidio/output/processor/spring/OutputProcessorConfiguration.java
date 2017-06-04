package presidio.output.processor.spring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.processor.services.OutputProcessServiceImpl;

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
