package fortscale.ml.model.retriever;

import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventConfService;
import net.minidev.json.JSONObject;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class EntityEventValueRetrieverTestUtils {
	@Autowired
	private EntityEventConfService entityEventConfService;

	private static final String FULL_AGGREGATED_FEATURE_EVENT_NAME = "featureBucket.featureName";

	protected EntityEventConf registerEntityEventConf(AbstractEntityEventValueRetrieverConf config) {
		String entityEventConfName = "entityEventConfName";
		EntityEventConf entityEventConf = Mockito.mock(EntityEventConf.class);
		when(entityEventConf.getName()).thenReturn(entityEventConfName);
		when(config.getEntityEventConfName()).thenReturn(entityEventConfName);
		when(entityEventConfService.getEntityEventConf(config.getEntityEventConfName())).thenReturn(entityEventConf);

		Map<String, Object> entityEventFunction = new HashMap<>();
		entityEventFunction.put("clusters", Collections.emptyMap());
		entityEventFunction.put("alphas", Collections.emptyMap());
		entityEventFunction.put("betas", Collections.singletonMap(FULL_AGGREGATED_FEATURE_EVENT_NAME, 1.0));
		when(entityEventConf.getEntityEventFunction()).thenReturn(new JSONObject(entityEventFunction));

		return entityEventConf;
	}

	protected String getFullAggregatedFeatureEventNameWithWeightOfOne() {
		return FULL_AGGREGATED_FEATURE_EVENT_NAME;
	}
}
