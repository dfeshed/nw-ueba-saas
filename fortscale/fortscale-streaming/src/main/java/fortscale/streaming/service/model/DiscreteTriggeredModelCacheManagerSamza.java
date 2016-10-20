package fortscale.streaming.service.model;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.model.store.ModelDAO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class DiscreteTriggeredModelCacheManagerSamza extends LazyTriggeredModelCacheManagerSamza {
	private static final String WRONG_MODEL_TYPE_ERROR_MSG = String.format(
			"Model must be of type %s", CategoryRarityModel.class.getSimpleName());
	private static final String NULL_FEATURE_ERROR_MSG = String.format(
			"%s cannot be null", Feature.class.getSimpleName());
	private static final String WRONG_FEATURE_VALUE_TYPE_ERROR_MSG_FORMAT =
			"Value of feature %s must be of type " + FeatureStringValue.class.getSimpleName();

	public DiscreteTriggeredModelCacheManagerSamza(String levelDbStoreName, ModelConf modelConf) {
		super(levelDbStoreName, modelConf);
	}

	@Override
	protected ModelDAO getModelDao(Feature feature, Map<String, String> context, long eventEpochtime) {
		ModelDAO modelDao = super.getModelDao(feature, context, eventEpochtime);
		if (modelDao != null) updateModelDao(modelDao, feature);
		return modelDao;
	}

	private void updateModelDao(ModelDAO modelDao, Feature feature) {
		CategoryRarityModel categoryRarityModel = castModel(modelDao.getModel());
		validateFeatureAndReplacePattern(feature);
		String featureValue = feature.getValue().toString();

		if (categoryRarityModel.getFeatureCount(featureValue) == null &&
				categoryRarityModel.isModelLoadedWithNumberOfEntries()) {
			Object data = retriever.retrieve(modelDao.getContextId(), modelDao.getEndTime(), feature);
			Double featureCounter = getFeatureCounter(data);

			if (featureCounter != null) {
				categoryRarityModel.setFeatureCount(featureValue, featureCounter);
				ModelsCacheInfo modelsCacheInfo = getModelsCacheInfo(modelDao.getContextId());
				modelsCacheInfo.setModelDao(modelDao);
				setModelsCacheInfo(modelDao.getContextId(), modelsCacheInfo);
			}
		}
	}

	private CategoryRarityModel castModel(Model model) {
		boolean isWrongModelType = CategoryRarityModel.class.isInstance(model);
		if(!isWrongModelType)
		{
			getMetrics().discreteCacheWrongModelType++;
			Assert.isTrue(isWrongModelType,WRONG_MODEL_TYPE_ERROR_MSG);
		}
		return (CategoryRarityModel)model;
	}

	private void validateFeatureAndReplacePattern(Feature feature) {
		if(feature==null)
		{
			getMetrics().discreteCacheNullFeature++;
		}
		Assert.notNull(feature, NULL_FEATURE_ERROR_MSG);
		String result;

		if (feature.getValue() == null) {
			result = retriever.replacePattern(StringUtils.EMPTY);
		} else if (feature.getValue() instanceof FeatureStringValue) {
			result = retriever.replacePattern(feature.getValue().toString());
		} else {
			getMetrics().discreteCacheWrongFeatureValue++;
			String errorMsg = String.format(WRONG_FEATURE_VALUE_TYPE_ERROR_MSG_FORMAT, feature.getName());
			throw new IllegalArgumentException(errorMsg);
		}

		feature.setValue(new FeatureStringValue(result));
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
