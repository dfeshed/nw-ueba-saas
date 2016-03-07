package fortscale.ml.scorer.config;

public class TestScorerConfService extends ScorerConfService {
	private String baseConfJsonFilesPath;

	public TestScorerConfService(String baseConfJsonFilesPath) {
		this.baseConfJsonFilesPath = baseConfJsonFilesPath;
	}

	@Override
	public String getBaseConfJsonFilesPath() {
		return baseConfJsonFilesPath;
	}

	@Override
	public String getBaseOverridingConfJsonFolderPath() {
		return null;
	}

	@Override
	public String getAdditionalConfJsonFolderPath() {
		return null;
	}
}
