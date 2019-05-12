package presidio.output.manager;

import fortscale.common.shell.PresidioShellableApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.output.manager.config.OutputManagerConfiguration;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class FortscaleOutputManagerApplication {

    public static void main(String[] args) {
        List<Class> sources = new ArrayList<Class>();
        // The supported CLI commands for the application
        sources.add(OutputManagerShellCommands.class);
        // The Spring configuration of the application
        sources.add(OutputManagerConfiguration.class);

        new PresidioShellableApplication().run(sources, args);
    }
}
