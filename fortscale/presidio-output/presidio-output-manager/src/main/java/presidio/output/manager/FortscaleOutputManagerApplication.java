package presidio.output.manager;

import fortscale.common.shell.PresidioShellableApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.output.manager.config.OutputManagerServiceConfig;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class FortscaleOutputManagerApplication {

    public static void main(String[] args) {
        List<Class> configurationClasses = new ArrayList<>();
        // The Spring configuration of the application
        configurationClasses.add(OutputManagerServiceConfig.class);
        new PresidioShellableApplication().run(configurationClasses, args);
    }
}
