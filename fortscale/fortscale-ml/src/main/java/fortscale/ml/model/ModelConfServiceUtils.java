package fortscale.ml.model;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
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
	private ModelConfService modelConfService;

	public Map<String, Collection<String>> getBucketConfNameToModelConfNamesMap(String dataSource) {
		Map<String, Collection<String>> map = new HashMap<>();

		bucketConfigurationService.getFeatureBucketConfs().forEach(bucketConf -> {
			if (bucketConf.getDataSources().contains(dataSource)) {
				map.put(bucketConf.getName(), new ArrayList<>());
			}
		});

		modelConfService.getModelConfs().forEach(modelConf -> {
			if (modelConf.getDataRetrieverConf() instanceof ContextHistogramRetrieverConf) {
				String bucketConfName = ((ContextHistogramRetrieverConf)modelConf.getDataRetrieverConf())
						.getFeatureBucketConfName();
				map.get(bucketConfName).add(modelConf.getName());
			}
		});

		return map;
	}
}
