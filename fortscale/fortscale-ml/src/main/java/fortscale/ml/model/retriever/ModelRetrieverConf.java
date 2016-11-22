package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

public class ModelRetrieverConf extends AbstractDataRetrieverConf {
	public static final String MODEL_RETRIEVER = "model_retriever";

	private String modelConfName;

	@JsonCreator
	public ModelRetrieverConf(
			@JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
			@JsonProperty("functions") List<JSONObject> functions,
			@JsonProperty("modelConfName") String modelConfName) {
		super(timeRangeInSeconds, functions);
		Assert.hasText(modelConfName);
		this.modelConfName = modelConfName;
	}

	@Override
	public String getFactoryName() {
		return MODEL_RETRIEVER;
	}

	public String getModelConfName() {
		return modelConfName;
	}
}
