package presidio.ade.processes.shell.config;

import fortscale.ml.model.metrics.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({
        // common application confs
        MaxContinuousModelRetrieverMetricsContainerConfig.class,
        CategoryRarityModelRetrieverMetricsContainerConfig.class,
        TimeModelRetrieverMetricsContainerConfig.class
})
public class RetrieverMetricsContainerConfig {

}
