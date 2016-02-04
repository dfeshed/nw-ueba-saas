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

	@Override
	public String loadScorerConfigurationsLocationPath() {
		return getPath("fortscale.scorer.configurations.location.path");
	}

	@Override
	public String loadScorerConfigurationsOverridingPath() {
		return getPath("fortscale.scorer.configurations.location.overriding.path");
	}

	@Override
	public String loadScorerConfigurationsAdditionalPath() {
		return getPath("fortscale.scorer.configurations.location.additional.path");
	}

	@Override
	public void afterPropertiesSet() {}

	@Override
	public void afterSamzaContainerInitialized() {
		loadConfs();
	}

	private String getPath(String key) {
		String path = fortscaleValueResolver.resolveStringValue(samzaContainerService.getConfig(), key);
		Assert.hasText(path);
		return path;
	}
}
