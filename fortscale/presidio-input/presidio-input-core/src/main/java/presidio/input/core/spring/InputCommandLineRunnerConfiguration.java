package presidio.input.core.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.core.services.api.InputExecutionService;

@Configuration
@Import({InputCoreConfiguration.class})
public class InputCommandLineRunnerConfiguration {

    @Autowired
    private InputExecutionService inputProcessService;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new PresidioCommandLineRunner(inputProcessService);
    }

    private static class PresidioCommandLineRunner implements CommandLineRunner {

        private InputExecutionService inputExecutionService;

        public PresidioCommandLineRunner(InputExecutionService inputExecutionService) {
            this.inputExecutionService = inputExecutionService;
        }

        @Override
        public void run(String... params) throws Exception {
            this.inputExecutionService.run(params);

        }
    }
}
