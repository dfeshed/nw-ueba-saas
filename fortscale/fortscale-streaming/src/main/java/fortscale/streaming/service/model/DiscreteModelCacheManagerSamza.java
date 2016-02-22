package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.store.ModelDAO;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class DiscreteModelCacheManagerSamza extends LazyModelCacheManagerSamza {
	public DiscreteModelCacheManagerSamza(String levelDbStoreName, ModelConf modelConf) {
		super(levelDbStoreName, modelConf);
	}

	@Override
	protected ModelDAO getModelDao(Feature feature, Map<String, Feature> context, long eventEpochtime) {
		ModelDAO modelDao = super.getModelDao(feature, context, eventEpochtime);
		if (modelDao != null) updateModelDao(modelDao, feature);
		return modelDao;
	}

	private void updateModelDao(ModelDAO modelDao, Feature feature) {
		CategoryRarityModelWithFeatureOccurrencesData discreteModel = castModel(modelDao.getModel());
		feature = new Feature(feature.getName(), retriever.replacePattern(feature.getValue().toString()));

		if (discreteModel.getFeatureCount(feature) == null) {
			Object data = retriever.retrieve(modelDao.getContextId(), modelDao.getEndTime(), feature);
			Double featureCounter = getFeatureCounter(data);

			if (featureCounter != null) {
				discreteModel.setFeatureCount(feature, featureCounter);
				ModelsCacheInfo modelsCacheInfo = getModelsCacheInfo(modelDao.getContextId());
				modelsCacheInfo.setModelDao(modelDao);
				setModelsCacheInfo(modelDao.getContextId(), modelsCacheInfo);
			}
		}
	}

	private CategoryRarityModelWithFeatureOccurrencesData castModel(Model model) {
		if (model instanceof CategoryRarityModelWithFeatureOccurrencesData) {
			return (CategoryRarityModelWithFeatureOccurrencesData)model;
		} else {
			throw new IllegalArgumentException(String.format(
					"Model must be of type %s",
					CategoryRarityModelWithFeatureOccurrencesData.class.getSimpleName()));
		}
	}

	private Double getFeatureCounter(Object data) {
		if (data instanceof GenericHistogram) {
			GenericHistogram histogram = (GenericHistogram)data;
			List<Map.Entry<String, Double>> entries = new ArrayList<>(
					histogram.getHistogramMap().entrySet());

			if (entries.size() == 1) {
				return entries.get(0).getValue();
			}
		}

		return null;
	}
}
