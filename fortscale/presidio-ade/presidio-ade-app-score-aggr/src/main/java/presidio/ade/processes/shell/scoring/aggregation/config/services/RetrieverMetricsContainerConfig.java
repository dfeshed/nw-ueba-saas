package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.FeatureBucketReaderConfig;
import fortscale.ml.model.metrics.CategoryRarityModelRetrieverMetricsContainerConfig;
import fortscale.ml.model.metrics.MaxContinuousModelRetrieverMetricsContainerConfig;
import fortscale.ml.model.metrics.TimeModelRetrieverMetricsContainerConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.factories.CategoricalFeatureValueRetrieverFactory;
import fortscale.ml.model.retriever.factories.ContextHistogramRetrieverFactory;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReaderConfig;

import java.util.Collection;

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
