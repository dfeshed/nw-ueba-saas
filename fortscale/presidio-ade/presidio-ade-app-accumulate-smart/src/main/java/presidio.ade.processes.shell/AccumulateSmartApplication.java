package presidio.ade.processes.shell;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.ade.processes.shell.config.AccumulateSmartConfiguration;
import presidio.ade.processes.shell.config.AccumulateSmartConfigurationProduction;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class AccumulateSmartApplication {
    private static final Logger logger = Logger.getLogger(AccumulateSmartConfiguration.class);


    public static void main(String[] args) {
        List<Class> sources = new ArrayList<Class>();
        // The supported CLI commands for the application
        sources.add(AccumulateSmartServiceCommands.class);
        // The Spring configuration of the application
        sources.add(AccumulateSmartConfigurationProduction.class);

        PresidioShellableApplication.run(sources, args);
    }
}




