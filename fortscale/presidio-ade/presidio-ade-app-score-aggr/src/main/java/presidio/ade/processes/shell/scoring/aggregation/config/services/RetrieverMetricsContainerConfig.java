package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.ml.model.metrics.CategoryRarityModelRetrieverMetricsContainerConfig;
import fortscale.ml.model.metrics.MaxContinuousModelRetrieverMetricsContainerConfig;
import fortscale.ml.model.metrics.TimeModelRetrieverMetricsContainerConfig;
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
