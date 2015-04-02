package fortscale.streaming.scorer;

import static fortscale.streaming.ConfigUtils.getConfigString;

import net.minidev.json.JSONObject;

import org.apache.samza.config.Config;

import fortscale.streaming.feature.extractor.FeatureExtractionService;

public abstract class AbstractScorer implements Scorer{
	protected String scorerName;
	protected String outputFieldName;
	protected FeatureExtractionService featureExtractionService;
	
	public AbstractScorer(String scorerName, Config config, ScorerContext context){
		this.scorerName = scorerName;
		outputFieldName = getConfigString(config, String.format("fortscale.score.%s.output.field.name", scorerName));
		
		this.featureExtractionService = (FeatureExtractionService) context.resolve(FeatureExtractionService.class, "featureExtractionService");
	}
	
	public Object extractFeature(String featureName, JSONObject eventMessage){
		return featureExtractionService.extract(featureName, eventMessage);
	}
}
