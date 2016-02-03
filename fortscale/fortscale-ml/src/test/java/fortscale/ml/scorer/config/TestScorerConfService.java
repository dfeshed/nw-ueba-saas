package fortscale.ml.scorer.config;

public class TestScorerConfService extends ScorerConfService {
	@Override
	public String loadScorerConfigurationsLocationPath() {
		return "classpath:config/asl/scorers/raw-events/*.json";
	}

	@Override
	public String loadScorerConfigurationsOverridingPath() {
		return "file:/home/cloudera/fortscale/config/asl/scorers/raw-events/*.json";
	}

	@Override
	public String loadScorerConfigurationsAdditionalPath() {
		return "file:/home/cloudera/fortscale/config/asl/scorers/raw-events/additional/*.json";
	}
}
