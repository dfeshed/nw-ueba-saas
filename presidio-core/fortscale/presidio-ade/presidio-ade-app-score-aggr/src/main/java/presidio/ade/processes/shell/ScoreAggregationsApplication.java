package presidio.ade.processes.shell;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import presidio.ade.processes.shell.scoring.aggregation.config.application.ScoreAggregationsApplicationConfigProduction;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@ComponentScan(excludeFilters = @Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class ScoreAggregationsApplication {
	private static final Logger logger = Logger.getLogger(ScoreAggregationsApplication.class);

	public static void main(String[] args) {
		logger.info("Starting {}.", ScoreAggregationsApplication.class.getSimpleName());
		List<Class> sources = Stream.of(ScoreAggregationsApplicationConfigProduction.class).collect(Collectors.toList());

		// The supported CLI commands for the application
		sources.add(PresidioCommands.class);

		new PresidioShellableApplication().run(sources, args);
	}
}
