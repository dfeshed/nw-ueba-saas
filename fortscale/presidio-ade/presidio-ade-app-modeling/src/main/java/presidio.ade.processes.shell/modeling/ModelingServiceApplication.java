package presidio.ade.processes.shell.modeling;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.common.shell.command.FSBannerProvider;
import fortscale.common.shell.command.FSHistoryFileNameProvider;
import fortscale.common.shell.command.FSPromptProvider;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(excludeFilters = @Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class ModelingServiceApplication {
	private static final Logger logger = Logger.getLogger(ModelingServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting {}.", ModelingServiceApplication.class.getSimpleName());
		List<Class> sources = new ArrayList<Class>();

		// The supported CLI commands for the application
		sources.add(ModelingServiceCommands.class);

		// The Spring configuration of the application
		sources.add(ModelingServiceConfiguration.class);

		// TODO Instant converter

		PresidioShellableApplication.run(sources, args);
	}
}
