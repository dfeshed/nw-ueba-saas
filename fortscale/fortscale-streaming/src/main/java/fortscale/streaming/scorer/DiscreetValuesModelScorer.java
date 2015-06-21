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
	protected double calculateCertainty(PrevalanceModel model) {
		double certainty = super.calculateCertainty(model);
		if(enoughNumOfDiscreetValuesToInfluence < 2){
			return certainty;
		}
		
		FieldModel fieldModel = model.getFieldModel(featureFieldName);
		if(!(fieldModel instanceof DiscreetValuesCalibratedModel)){
			return certainty;
		}
		
		
		DiscreetValuesCalibratedModel calibratedModel = (DiscreetValuesCalibratedModel) fieldModel;
		int numOfFeatureValues = calibratedModel.getNumOfFeatureValues();
		double discreetCertainty = 0;
		if(numOfFeatureValues >= enoughNumOfDiscreetValuesToInfluence){
			discreetCertainty = 1;
		} else if(numOfFeatureValues >= minNumOfDiscreetValuesToInfluence){
			discreetCertainty = ((double)(numOfFeatureValues - minNumOfDiscreetValuesToInfluence + 1)) / (enoughNumOfDiscreetValuesToInfluence - minNumOfDiscreetValuesToInfluence + 1);
		}
		
		
		return certainty*discreetCertainty;
	}
}
