package fortscale.ml.model.metrics;

import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

/**
 * Metrics on category rarity model retriever
 */
public class TimeModelRetrieverMetricsContainer extends ModelRetrieverMetricsContainer {
    public static final String METRIC_NAME = "time_model_retriever";

    /**
     * @param metricCollectingService
     * @param metricsExporter
     */
    public TimeModelRetrieverMetricsContainer(MetricCollectingService metricCollectingService, MetricsExporter metricsExporter) {
        super(metricCollectingService, metricsExporter, METRIC_NAME);
    }

    @Override
    public String getFactoryName() {
        return ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER;
    }
}
