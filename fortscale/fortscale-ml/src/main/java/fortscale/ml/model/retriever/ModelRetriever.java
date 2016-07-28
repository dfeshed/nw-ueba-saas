package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Configurable(preConstruction = true)
public class ModelRetriever extends AbstractDataRetriever {
	@Autowired
	private ModelConfService modelConfService;
	@Autowired
	private ModelStore modelStore;

	private ModelConf modelConf;

	public ModelRetriever(ModelRetrieverConf config) {
		super(config);

		modelConf = modelConfService.getModelConf(config.getModelConfName());
		Assert.notNull(modelConf);
	}

	@Override
	public List<Model> retrieve(String contextId, Date endTime) {
		Assert.isNull(contextId, String.format("%s can't be used with a context", getClass().getSimpleName()));
		return modelStore.getAllContextsModelDaosWithLatestEndTimeLte(modelConf, endTime.getTime() / 1000).stream()
				.map(ModelDAO::getModel)
				.collect(Collectors.toList());
	}

	@Override
	public Object retrieve(String contextId, Date endTime, Feature feature) {
		throw new UnsupportedOperationException(String.format("%s does not support retrieval of a single feature",
				getClass().getSimpleName()));
	}

	@Override
	public Set<String> getEventFeatureNames() {
		throw new UnsupportedOperationException(String.format("%s should be used to create \"additional-models\" only",
				getClass().getSimpleName()));
	}

	@Override
	public List<String> getContextFieldNames() {
		return Collections.emptyList();
	}

	@Override
	public String getContextId(Map<String, String> context) {
		return null;
	}
}
