package presidio.ade.modeling;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import presidio.ade.modeling.config.ModelingServiceConfigurationProduction;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(excludeFilters = @Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class ModelingServiceApplication {
	private static final Logger logger = Logger.getLogger(ModelingServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting {}.", ModelingServiceApplication.class.getSimpleName());
		List<Class> configurationClasses = new ArrayList<>();
		configurationClasses.add(ModelingServiceConfigurationProduction.class);
		new PresidioShellableApplication().run(configurationClasses, args);
	}
}
