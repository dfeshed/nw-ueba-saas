package fortscale.streaming.service;

public class EventFeatureScorerConfig {
	private String scoreFieldName;
	private String modelName;
	private String contextFieldName;
	private String featureFieldName;
	
	
	public EventFeatureScorerConfig(String scoreFieldName, String modelName, String contextFieldName, String featureFieldName){
		this.scoreFieldName = scoreFieldName;
		this.modelName = modelName;
		this.contextFieldName = contextFieldName;
		this.featureFieldName = featureFieldName;
	}
	
	public String getModelName() {
		return modelName;
	}
	public String getContextFieldName() {
		return contextFieldName;
	}
	public String getFeatureFieldName() {
		return featureFieldName;
	}

	public String getScoreFieldName() {
		return scoreFieldName;
	}
	
}
