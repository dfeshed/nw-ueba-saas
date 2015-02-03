package fortscale.streaming.scorer;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.model.prevalance.field.DiscreetValuesCalibratedModel;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;

public class DiscreetValuesModelScorer extends ModelScorer {
	
	private int minNumOfDiscreetValuesToInfluence;
	private int enoughNumOfDiscreetValuesToInfluence;

	public DiscreetValuesModelScorer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);
		minNumOfDiscreetValuesToInfluence = config.getInt(String.format("fortscale.score.%s.discreet.values.to.influence.min", scorerName), 0);
		enoughNumOfDiscreetValuesToInfluence = Math.max(config.getInt(String.format("fortscale.score.%s.discreet.values.to.influence.enough", scorerName), 0), minNumOfDiscreetValuesToInfluence);
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		if(enoughNumOfDiscreetValuesToInfluence < 2){
			return super.calculateScore(eventMessage);
		}
		
		
		
		// get the context, so that we can get the model
		String context = eventMessage.getEventStringValue(contextFieldName);
		if (StringUtils.isEmpty(context)) {
			throw new StreamMessageNotContainFieldException(eventMessage.toJSONString(), contextFieldName);
		}
		
		// go over each field in the event and add it to the model
		PrevalanceModel model = modelService.getModel(context, modelName);
		
		FieldModel fieldModel = model.getFieldModel(featureFieldName);
		if(!(fieldModel instanceof DiscreetValuesCalibratedModel)){
			return super.calculateScore(eventMessage);
		}
		
		
		DiscreetValuesCalibratedModel calibratedModel = (DiscreetValuesCalibratedModel) fieldModel;
		int numOfFeatureValues = calibratedModel.getNumOfFeatureValues();
		double certainty = 0;
		if(numOfFeatureValues >= enoughNumOfDiscreetValuesToInfluence){
			certainty = 1;
		} else if(numOfFeatureValues >= minNumOfDiscreetValuesToInfluence){
			certainty = ((double)(numOfFeatureValues - minNumOfDiscreetValuesToInfluence + 1)) / (enoughNumOfDiscreetValuesToInfluence - minNumOfDiscreetValuesToInfluence + 1);
		}
		
		double score = 0;
		if(model != null){
			score = model.calculateScore(eventMessage.getJsonObject(), featureFieldName);
		}
		
		return new ModelFeatureScore(outputFieldName, score, certainty);
	}
}
