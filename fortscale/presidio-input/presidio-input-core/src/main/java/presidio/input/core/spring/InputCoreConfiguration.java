package presidio.input.core.spring;


import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.core.services.api.InputProcessService;
import presidio.input.core.services.impl.InputProcessServiceImpl;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import(MongoConfig.class)
public class InputCoreConfiguration {

    @Bean
    public InputProcessService inputProcessService(){
        return new InputProcessServiceImpl();
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new PresidioCommandLineRunner(inputProcessService());
    }




    private static class PresidioCommandLineRunner implements CommandLineRunner {

        private InputProcessService inputProcessService;

        public PresidioCommandLineRunner(InputProcessService inputProcessService){
            this.inputProcessService = inputProcessService;
        }

        @Override
        public void run(String... params) throws Exception {
            this.inputProcessService.run(params);

        }
    }

}
