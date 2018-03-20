package presidio.output.forwarder.shell;

import fortscale.common.shell.PresidioShellableApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.output.forwarder.spring.OutputForwarderBeans;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class OutputForwarderApplication {

    public static void main(String[] args) {
        List<Class> sources = new ArrayList<>();
        // The supported CLI commands for the application
        sources.add(presidio.output.forwarder.shell.OutputForwarderServiceCommands.class);
        // The Spring configuration of the application
        sources.add(OutputForwarderBeans.class);

        new PresidioShellableApplication().run(sources, args);
    }
}




