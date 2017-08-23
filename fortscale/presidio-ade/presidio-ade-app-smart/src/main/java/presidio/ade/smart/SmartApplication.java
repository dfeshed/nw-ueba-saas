package presidio.ade.smart;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import presidio.ade.smart.config.SmartApplicationConfigurationProduction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lior Govrin
 */
@SpringBootApplication
@ComponentScan(excludeFilters = @Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class SmartApplication {
	private static final Logger logger = Logger.getLogger(SmartApplication.class);

	public static void main(String[] args) {
		logger.info("Starting {}.", SmartApplication.class.getSimpleName());
		List<Class> configurationClasses = new ArrayList<>();
		configurationClasses.add(SmartApplicationConfigurationProduction.class);
		PresidioShellableApplication.run(configurationClasses, args);
	}
}
