package presidio.input.core;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.common.shell.command.PresidioCommands;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import presidio.input.core.spring.InputProductionConfiguration;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(excludeFilters = @Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class FortscaleInputCoreApplication {
    public static void main(String[] args) {
        List<Class> configurationClasses = new ArrayList<>();
        configurationClasses.add(InputProductionConfiguration.class);
        // The supported CLI commands for the application.
        configurationClasses.add(PresidioCommands.class);
        new PresidioShellableApplication().run(configurationClasses, args);
    }
}
