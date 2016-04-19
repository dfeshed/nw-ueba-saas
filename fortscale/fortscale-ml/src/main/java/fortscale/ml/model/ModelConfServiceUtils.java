package fortscale.ml.model;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.retriever.AggregatedFeatureValueRetrieverConf;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModelConfServiceUtils {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private ModelConfService modelConfService;

	public Map<String, Collection<ModelConf>> getBucketConfNameToModelConfsMap(String dataSource) {
		Map<String, Collection<ModelConf>> map = new HashMap<>();

		bucketConfigurationService.getFeatureBucketConfs().forEach(bucketConf -> {
			if (bucketConf.getDataSources().contains(dataSource)) {
				map.put(bucketConf.getName(), new ArrayList<>());
			}
		});

		modelConfService.getModelConfs().forEach(modelConf -> {
			if (modelConf.getDataRetrieverConf() instanceof ContextHistogramRetrieverConf) {
				String bucketConfName = ((ContextHistogramRetrieverConf)modelConf.getDataRetrieverConf())
						.getFeatureBucketConfName();
				if (map.containsKey(bucketConfName)) map.get(bucketConfName).add(modelConf);
			}
		});

		return map;
	}

	public Map<String, Collection<ModelConf>> getAggrEventConfNameToModelConfsMap() {
		Map<String, Collection<ModelConf>> map = new HashMap<>();

		aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().forEach(aggregatedFeatureEventConf -> {
			map.put(aggregatedFeatureEventConf.getName(), new ArrayList<>());
		});

		modelConfService.getModelConfs().forEach(modelConf -> {
			if (modelConf.getDataRetrieverConf() instanceof AggregatedFeatureValueRetrieverConf) {
				String aggrEventConfName = ((AggregatedFeatureValueRetrieverConf)modelConf.getDataRetrieverConf())
						.getAggregatedFeatureEventConfName();
				if (map.containsKey(aggrEventConfName)) map.get(aggrEventConfName).add(modelConf);
			}
		});

		return map;
	}
}
