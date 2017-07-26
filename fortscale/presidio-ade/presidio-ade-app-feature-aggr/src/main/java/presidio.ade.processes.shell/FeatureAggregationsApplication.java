package presidio.ade.processes.shell;

import fortscale.common.general.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.Arrays;


@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class FeatureAggregationsApplication extends PresidioShellableApplication {
    private static final Logger logger = Logger.getLogger(FeatureAggregationsApplication.class);


    public static void main(String[] args) {
        logger.info("Start application: {} with params {}", FeatureAggregationsConfigProduction.class, Arrays.toString(args));
        PresidioShellableApplication.run(FeatureAggregationsConfigProduction.class, args);
    }
}




