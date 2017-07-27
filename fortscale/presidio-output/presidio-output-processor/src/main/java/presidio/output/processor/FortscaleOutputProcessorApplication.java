package presidio.output.processor;



import fortscale.common.general.PresidioShellableApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.output.processor.spring.OutputProcessorConfiguration;


@SpringBootApplication
@ComponentScan(
		excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
							@ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
public class FortscaleOutputProcessorApplication {

	private static  Logger logger = LoggerFactory.getLogger(FortscaleOutputProcessorApplication.class);

	public static void main(String[] args) {
		logger.info("Starting {}.", FortscaleOutputProcessorApplication.class.getSimpleName());
		PresidioShellableApplication.run(new Object[]{FortscaleOutputProcessorApplication.class, OutputProcessorConfiguration.class}, args);


	}


}
