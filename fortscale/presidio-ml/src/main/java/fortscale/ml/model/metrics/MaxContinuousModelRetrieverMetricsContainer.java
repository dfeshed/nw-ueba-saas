package fortscale.ml.model.metrics;

import fortscale.ml.model.builder.gaussian.ContinuousMaxHistogramModelBuilderConf;
import fortscale.ml.model.retriever.CategoricalFeatureValueRetrieverConf;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

/**
 * Metrics on max continuous model retriever
 */
public class MaxContinuousModelRetrieverMetricsContainer extends ModelRetrieverMetricsContainer {
    public static final String METRIC_NAME = "max_continuous_model_retriever";

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public MaxContinuousModelRetrieverMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        super(metricCollectingService, metricsExporter, METRIC_NAME);
    }

    @Override
    public String getFactoryName() {
        return ContinuousMaxHistogramModelBuilderConf.CONTINUOUS_MAX_HISTOGRAM_MODEL_BUILDER;
    }
}
