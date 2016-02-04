package fortscale.ml.scorer.config;

public class TestScorerConfService extends ScorerConfService {
	@Override
	public String getBaseConfJsonFilesPath() {
		return "classpath:config/asl/scorers/raw-events/*.json";
	}

	@Override
	public String getBaseOverridingConfJsonFolderPath() {
		return "file:/home/cloudera/fortscale/config/asl/scorers/raw-events/*.json";
	}

	@Override
	public String getAdditionalConfJsonFolderPath() {
		return "file:/home/cloudera/fortscale/config/asl/scorers/raw-events/additional/*.json";
	}
}
