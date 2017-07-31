package presidio.ade.processes.shell;

import fortscale.common.shell.PresidioShellableApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class FeatureAggregationsApplication {


    public static void main(String[] args) {
        List<Class> sources = Stream.of(FeatureAggregationsConfigProduction.class).collect(Collectors.toList());
        PresidioShellableApplication.run(sources, args);
    }
}




