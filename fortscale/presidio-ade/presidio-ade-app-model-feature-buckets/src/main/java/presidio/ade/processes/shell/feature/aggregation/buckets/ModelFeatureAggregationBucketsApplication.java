package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.common.shell.command.PresidioCommands;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import presidio.ade.processes.shell.feature.aggregation.buckets.config.ModelFeatureAggregationBucketsConfigurationProduction;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@ComponentScan(excludeFilters = @Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class ModelFeatureAggregationBucketsApplication {

	public static void main(String[] args) {
		List<Class> sources = Stream.of(ModelFeatureAggregationBucketsConfigurationProduction.class).collect(Collectors.toList());

		// The supported CLI commands for the application
		sources.add(PresidioCommands.class);

		new PresidioShellableApplication().run(sources, args);
	}
}
