package presidio.output.forwarder.shell;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.output.forwarder.config.OutputForwarderConfiguration;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class OutputForwarderApplication {
    private static final Logger logger = Logger.getLogger(OutputForwarderApplication.class);


    public static void main(String[] args) {
        List<Class> sources = new ArrayList<Class>();
        // The supported CLI commands for the application
        sources.add(presidio.output.forwarder.shell.OutputForwarderServiceCommands.class);
        // The Spring configuration of the application
        sources.add(OutputForwarderConfiguration.class);

        new PresidioShellableApplication().run(sources, args);
    }
}




