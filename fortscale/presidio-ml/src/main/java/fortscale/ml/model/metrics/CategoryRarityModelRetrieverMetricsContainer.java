package fortscale.ml.model.metrics;

import fortscale.ml.model.retriever.CategoricalFeatureValueRetrieverConf;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

/**
 * Metrics on category rarity model retriever
 */
public class CategoryRarityModelRetrieverMetricsContainer extends ModelRetrieverMetricsContainer {
    public static final String METRIC_NAME = "category_rarity_model_retriever";

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public CategoryRarityModelRetrieverMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        super(metricCollectingService, metricsExporter, METRIC_NAME);
    }

    @Override
    public String getFactoryName() {
        return CategoricalFeatureValueRetrieverConf.FACTORY_NAME;
    }
}
