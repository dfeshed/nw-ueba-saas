package fortscale.ml.model.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Import({CategoryRarityModeBuilderMetricsContainerConfig.class,
        CategoryRarityModeRetrieverMetricsContainerConfig.class})
public class ModelingServiceMetricsContainerConfig
{
    @Autowired
    private MetricCollectingService metricCollectingService;
    @Autowired
    private MetricsExporter metricsExporter;

    private Map<String, IModelMetricsContainer> modelBuilderMetricsContainers;

    @Autowired
    public void setUpModelBuilderMetricsContainers(Set<IModelMetricsContainer> modelBuilderMetricsContainers){
        this.modelBuilderMetricsContainers = modelBuilderMetricsContainers.stream()
                .collect(Collectors.toMap(IModelMetricsContainer::getFactoryName, Function.identity()));
    }

    @Bean
    public ModelingServiceMetricsContainer modelingServiceMetricsContainer()
    {
        return new ModelingServiceMetricsContainer(metricCollectingService, metricsExporter, modelBuilderMetricsContainers);
    }
}
