package fortscale.streaming.service.scorer;

import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.streaming.common.SamzaContainerInitializedListener;
import fortscale.streaming.common.SamzaContainerService;
import fortscale.streaming.service.FortscaleValueResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class SamzaScorerConfService extends ScorerConfService implements SamzaContainerInitializedListener {
	@Autowired
	private SamzaContainerService samzaContainerService;
	@Autowired
	private FortscaleValueResolver fortscaleValueResolver;

	private String scorerConfigurationsLocationPath;
	private String scorerConfigurationsOverridingPath;
	private String scorerConfigurationsAdditionalPath;

	@Override
	public void afterPropertiesSet() {
		samzaContainerService.registerSamzaContainerInitializedListener(this);
	}

	@Override
	public void afterSamzaContainerInitialized() {
		scorerConfigurationsLocationPath = getPath("fortscale.scorer.configurations.location.path");
		scorerConfigurationsOverridingPath = getPath("fortscale.scorer.configurations.location.overriding.path");
		scorerConfigurationsAdditionalPath = getPath("fortscale.scorer.configurations.location.additional.path");
		loadConfs();
	}

	@Override
	protected String getBaseConfJsonFilesPath() {
		return scorerConfigurationsLocationPath;
	}

	@Override
	protected String getBaseOverridingConfJsonFolderPath() {
		return scorerConfigurationsOverridingPath;
	}

	@Override
	protected String getAdditionalConfJsonFolderPath() {
		return scorerConfigurationsAdditionalPath;
	}

	private String getPath(String key) {
		String path = fortscaleValueResolver.resolveStringValue(samzaContainerService.getConfig(), key);
		Assert.hasText(path);
		return path;
	}
}
