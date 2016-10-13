package fortscale.ml.model.retriever;

import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

public abstract class AbstractEntityEventValueRetrieverConf extends AbstractDataRetrieverConf {
	private String entityEventConfName;

	public AbstractEntityEventValueRetrieverConf(long timeRangeInSeconds,
												 List<JSONObject> functions,
												 String entityEventConfName) {
		super(timeRangeInSeconds, functions);
		Assert.hasText(entityEventConfName);
		this.entityEventConfName = entityEventConfName;
	}

	public String getEntityEventConfName() {
		return entityEventConfName;
	}
}
