package fortscale.ml.processes.shell.modeling;

import fortscale.common.general.PresidioShellableApplication;
import fortscale.common.shell.command.FSBannerProvider;
import fortscale.common.shell.command.FSHistoryFileNameProvider;
import fortscale.common.shell.command.FSPromptProvider;
import fortscale.ml.model.config.ModelingServiceConfiguration;
import fortscale.utils.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = @Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class ModelingServiceApplication {
	private static final Logger logger = Logger.getLogger(ModelingServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting {}.", ModelingServiceApplication.class.getSimpleName());
		Object[] sources = {
				// The supported CLI commands for the application
				ModelingServiceCommands.class,
				// The Spring configuration of the application
				ModelingServiceConfiguration.class,
				// TODO Instant converter
				// Required providers for the shell
				FSBannerProvider.class,
				FSHistoryFileNameProvider.class,
				FSPromptProvider.class
		};
		ConfigurableApplicationContext context = SpringApplication.run(sources, args);
		PresidioShellableApplication.run(args, context);
	}
}
