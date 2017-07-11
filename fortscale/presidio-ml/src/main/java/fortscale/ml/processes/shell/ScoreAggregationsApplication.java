package fortscale.ml.processes.shell;

import fortscale.common.general.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = @Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class ScoreAggregationsApplication {
	private static final Logger logger = Logger.getLogger(ScoreAggregationsApplication.class);

	public static void main(String[] args) {
		logger.info("Starting {}.", ScoreAggregationsApplication.class.getSimpleName());
		PresidioShellableApplication.run(ScoreAggregationsConfiguration.class, args);
	}
}
