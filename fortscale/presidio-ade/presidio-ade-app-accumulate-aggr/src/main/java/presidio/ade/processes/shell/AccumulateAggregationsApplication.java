package presidio.ade.processes.shell;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class AccumulateAggregationsApplication {
    private static final Logger logger = Logger.getLogger(AccumulateAggregationsConfig.class);


    public static void main(String[] args) {
        logger.info("Start application: {} with params {}", AccumulateAggregationsConfigProduction.class, Arrays.toString(args));
        List<Class> sources = Stream.of(AccumulateAggregationsConfigProduction.class).collect(Collectors.toList());
        PresidioShellableApplication.run(sources, args);
    }
}




