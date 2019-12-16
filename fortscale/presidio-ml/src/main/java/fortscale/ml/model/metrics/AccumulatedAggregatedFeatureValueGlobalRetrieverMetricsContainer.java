package fortscale.ml.model.metrics;

import fortscale.ml.model.builder.gaussian.ContinuousMaxHistogramModelBuilderConf;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

public class AccumulatedAggregatedFeatureValueGlobalRetrieverMetricsContainer extends ModelRetrieverMetricsContainer {
    public static final String METRIC_NAME = "accumulated_aggregated_feature_value_global_retriever";

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public AccumulatedAggregatedFeatureValueGlobalRetrieverMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        super(metricCollectingService, metricsExporter, METRIC_NAME);
    }

    @Override
    public String getFactoryName() {
        return ContinuousMaxHistogramModelBuilderConf.CONTINUOUS_MAX_HISTOGRAM_MODEL_BUILDER;
    }
}