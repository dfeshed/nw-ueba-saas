package fortscale.streaming.service;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;
import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;

public class EventFeatureScorer implements Scorer{
//	private static final Logger logger = Logger.getLogger(EventFeatureScorer.class);
	
	
	private ModelService modelService;
	private EventFeatureScorerConfig eventFeatureScorerConfig;

	public EventFeatureScorer(String scoreName, Config config,  ModelService modelService){
		String modelName = getConfigString(config, String.format("fortscale.score.%s.model.name", scoreName));
		String featureFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.fieldname", scoreName, modelName));
		String contextFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.context.fieldname", scoreName, modelName));
		eventFeatureScorerConfig = new EventFeatureScorerConfig(scoreName, modelName, contextFieldName, featureFieldName);
		this.modelService = modelService;
	}

	@Override
	public Double calculateScore(JSONObject jsonObject) throws Exception {
		// get the username, so that we can get the model from store
		String context = convertToString(jsonObject.get(eventFeatureScorerConfig.getContextFieldName()));
		if (StringUtils.isEmpty(context)) {
			throw new StreamMessageNotContainFieldException(jsonObject.toJSONString(), eventFeatureScorerConfig.getContextFieldName());
		}
		
		// go over each field in the event and add it to the model
		PrevalanceModel model = modelService.getModel(context, eventFeatureScorerConfig.getModelName());
		
		double score = model.calculateScore(jsonObject, eventFeatureScorerConfig.getFeatureFieldName());
		
		jsonObject.put(eventFeatureScorerConfig.getScoreFieldName(), score);
		
		if(model.shouldAffectEventScore(eventFeatureScorerConfig.getFeatureFieldName())){
			return score;
		}
		
		return null;
	}
}
