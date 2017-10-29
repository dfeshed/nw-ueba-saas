package presidio.output.processor;


import fortscale.common.shell.PresidioShellableApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import presidio.output.processor.spring.OutputProcessorConfiguration;

import java.util.ArrayList;
import java.util.List;


//@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
@Configuration
public class FortscaleOutputProcessorApplication {

	private static  Logger logger = LoggerFactory.getLogger(FortscaleOutputProcessorApplication.class);

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
