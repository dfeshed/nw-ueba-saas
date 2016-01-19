package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.store.ModelDAO;
import fortscale.streaming.ExtendedSamzaTaskContext;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class DiscreteModelCacheManagerSamza extends ModelCacheManagerSamza {
	public DiscreteModelCacheManagerSamza(ExtendedSamzaTaskContext context, ModelConf modelConf) {
		super(context, modelConf);
	}

	@Override
	protected void updateModelDao(ModelDAO modelDao, Feature feature) {
		CategoryRarityModelWithFeatureOccurrencesData discreteModel = castModel(modelDao.getModel());

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
