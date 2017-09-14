package presidio.ade.manager;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.ade.manager.config.AdeManagerApplicationConfigurationProduction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maria_dorohin on 9/6/17.
 */

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class AdeManagerApplication {

    private static final Logger logger = Logger.getLogger(AdeManagerApplication.class);

    public static void main(String[] args) {
        List<Class> sources = new ArrayList<Class>();
        // The supported CLI commands for the application
        sources.add(AdeManagerApplicationCommands.class);
        // The Spring configuration of the application
        sources.add(AdeManagerApplicationConfigurationProduction.class);

        PresidioShellableApplication.run(sources, args);
    }
}

