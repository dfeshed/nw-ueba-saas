package presidio.adapter;

import fortscale.common.general.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.adapter.config.AdapterConfiguration;

import java.util.Arrays;


@SpringBootApplication
@ComponentScan( //only scan for spring-boot beans
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
public class FortscaleAdapterApplication {
    private static final Logger logger = Logger.getLogger(FortscaleAdapterApplication.class);

    public static void main(String[] args) {
        logger.info("starting Adapter with params " + Arrays.toString(args));
        PresidioShellableApplication.run(AdapterConfiguration.class, args);
    }
}
