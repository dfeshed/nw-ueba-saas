package fortscale.streaming.service.scorer;

import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.streaming.common.SamzaContainerService;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.SpringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class SamzaScorerConfService extends ScorerConfService {
	@Autowired
	private SamzaContainerService samzaContainerService;

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

	private String getPath(String key) {
		FortscaleValueResolver resolver = SpringService.getInstance().resolve(FortscaleValueResolver.class);
		String path = resolver.resolveStringValue(samzaContainerService.getConfig(), key);
		Assert.hasText(path);
		return path;
	}
}
