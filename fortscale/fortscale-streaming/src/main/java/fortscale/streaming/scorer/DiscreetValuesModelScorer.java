package fortscale.streaming.scorer;

import org.apache.samza.config.Config;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.model.prevalance.field.DiscreetValuesCalibratedModel;

public class DiscreetValuesModelScorer extends ModelScorer {
	
	private int minNumOfDiscreetValuesToInfluence;
	private int enoughNumOfDiscreetValuesToInfluence;

	public DiscreetValuesModelScorer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);
		minNumOfDiscreetValuesToInfluence = config.getInt(String.format("fortscale.score.%s.discreet.values.to.influence.min", scorerName), 0);
		enoughNumOfDiscreetValuesToInfluence = Math.max(config.getInt(String.format("fortscale.score.%s.discreet.values.to.influence.enough", scorerName), 0), minNumOfDiscreetValuesToInfluence);
	}

	@Override
	protected FeatureScore calculateModelScore(EventMessage eventMessage, PrevalanceModel model) throws Exception {
		if(enoughNumOfDiscreetValuesToInfluence < 2){
			return super.calculateModelScore(eventMessage, model);
		}
		
		FieldModel fieldModel = model.getFieldModel(featureFieldName);
		if(!(fieldModel instanceof DiscreetValuesCalibratedModel)){
			return super.calculateModelScore(eventMessage, model);
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
			score = model.calculateScore(featureExtractionService, eventMessage.getJsonObject(), featureFieldName);
		}
		
		return new ModelFeatureScore(outputFieldName, score, certainty);
	}
}
