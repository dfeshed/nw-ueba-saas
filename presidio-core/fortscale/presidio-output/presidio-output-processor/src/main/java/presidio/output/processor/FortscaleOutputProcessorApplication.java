package presidio.output.processor;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.output.processor.spring.OutputProcessorConfiguration;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class FortscaleOutputProcessorApplication {
	private static Logger logger = Logger.getLogger(FortscaleOutputProcessorApplication.class);

	public static void main(String[] args) {
		logger.info("Starting {}.", FortscaleOutputProcessorApplication.class.getSimpleName());
		List<Class> sources = new ArrayList<>();

		// The Spring configuration of the application
		sources.add(OutputProcessorConfiguration.class);

		// The supported CLI commands for the application
		sources.add(OutputShellCommands.class);

		new PresidioShellableApplication().run(sources, args);
	}


}
