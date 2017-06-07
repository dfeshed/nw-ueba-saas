package presidio.input.core.services.impl;

import org.springframework.boot.CommandLineRunner;
import presidio.input.core.services.api.InputExecutionService;

/**
 * Created by maors on 6/7/2017.
 */
public class InputCommandLineRunner implements CommandLineRunner {

    private InputExecutionService inputExecutionService;

    public InputCommandLineRunner(InputExecutionService inputExecutionService) {
        this.inputExecutionService = inputExecutionService;
    }

    @Override
    public void run(String... params) throws Exception {
        inputExecutionService.init(params);
        inputExecutionService.run();
    }
}