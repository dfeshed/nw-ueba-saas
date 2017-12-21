package fortscale.ml.model.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Configuration
@Import({CategoryRarityModeBuilderMetricsContainerConfig.class,
        MaxContinuousModeBuilderMetricsContainerConfig.class,
        TimeModeBuilderMetricsContainerConfig.class,
        TimeModeBuilderMetricsPartitionsContainerConfig.class
})
public class ModelingServiceMetricsContainerConfig {
    @Autowired
    private MetricCollectingService metricCollectingService;
    @Autowired
    private MetricsExporter metricsExporter;
    private Map<String, List<IModelMetricsContainer>> modelMetricsContainers;

    @Autowired
    public void setUpModelBuilderMetricsContainers(Set<IModelMetricsContainer> modelBuilderMetricsContainers) {
        this.modelMetricsContainers = modelBuilderMetricsContainers.stream().collect(groupingBy(IModelMetricsContainer::getFactoryName, toList()));
    }

    @Bean
    public ModelingServiceMetricsContainer modelingServiceMetricsContainer() {
        return new ModelingServiceMetricsContainer(metricCollectingService, metricsExporter, modelMetricsContainers);
    }
}
