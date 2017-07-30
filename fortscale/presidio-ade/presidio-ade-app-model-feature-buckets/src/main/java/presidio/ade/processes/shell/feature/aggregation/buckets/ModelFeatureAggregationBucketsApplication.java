package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.common.shell.config.ShellableApplicationConfig;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@ComponentScan(excludeFilters = @Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class ModelFeatureAggregationBucketsApplication {
	private static final Logger logger = Logger.getLogger(ModelFeatureAggregationBucketsApplication.class);

	public static void main(String[] args) {
		logger.info("Starting {}.", ModelFeatureAggregationBucketsApplication.class.getSimpleName());
		List<Class> sources = Stream.of(ModelFeatureAggregationBucketsConfiguration.class).collect(Collectors.toList());
		PresidioShellableApplication.run(sources, args);
	}
}
