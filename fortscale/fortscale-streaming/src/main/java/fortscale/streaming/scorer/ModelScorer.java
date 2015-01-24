package fortscale.streaming.scorer;

import static fortscale.streaming.ConfigUtils.getConfigString;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;

public class ModelScorer implements Scorer{
//	private static final Logger logger = Logger.getLogger(EventFeatureScorer.class);
	
	
	private ModelService modelService;
	private ModelScorerConfig modelScorerConfig;

	public ModelScorer(String scoreName, Config config,  ModelService modelService){
		String modelName = getConfigString(config, String.format("fortscale.score.%s.model.name", scoreName));
		String featureFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.fieldname", scoreName, modelName));
		String contextFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.context.fieldname", scoreName, modelName));
		modelScorerConfig = new ModelScorerConfig(scoreName, modelName, contextFieldName, featureFieldName);
		this.modelService = modelService;
	}

	@Override
	public Double calculateScore(EventMessage eventMessage) throws Exception {
		// get the context, so that we can get the model
		String context = eventMessage.getEventStringValue(modelScorerConfig.getContextFieldName());
		if (StringUtils.isEmpty(context)) {
			throw new StreamMessageNotContainFieldException(eventMessage.toJSONString(), modelScorerConfig.getContextFieldName());
		}
		
		// go over each field in the event and add it to the model
		PrevalanceModel model = modelService.getModel(context, modelScorerConfig.getModelName());
		
		double score = model.calculateScore(eventMessage.getJsonObject(), modelScorerConfig.getFeatureFieldName());
		
		eventMessage.setScore(modelScorerConfig.getScoreFieldName(), score);
		
		return score;
	}

	@Override
	public void afterPropertiesSet(Map<String, Scorer> scorerMap) {}
}
