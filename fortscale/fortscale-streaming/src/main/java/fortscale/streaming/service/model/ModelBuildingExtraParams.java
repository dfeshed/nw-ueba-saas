package fortscale.streaming.service.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelBuildingExtraParams {
	private Map<String, String> managerParams;
	private Map<String, String> selectorParams;
	private Map<String, String> retrieverParams;
	private Map<String, String> builderParams;

	@JsonCreator
	public ModelBuildingExtraParams(@JsonProperty("managerParams") Map<String, String> managerParams,
									@JsonProperty("selectorParams") Map<String, String> selectorParams,
									@JsonProperty("retrieverParams") Map<String, String> retrieverParams,
									@JsonProperty("builderParams") Map<String, String> builderParams) {
		Assert.notNull(managerParams);
		Assert.notNull(selectorParams);
		Assert.notNull(retrieverParams);
		Assert.notNull(builderParams);
		this.managerParams = managerParams;
		this.selectorParams = selectorParams;
		this.retrieverParams = retrieverParams;
		this.builderParams = builderParams;
	}

	public Map<String, String> getManagerParams() {
		return managerParams;
	}

	public Map<String, String> getSelectorParams() {
		return selectorParams;
	}

	public Map<String, String> getRetrieverParams() {
		return retrieverParams;
	}

	public Map<String, String> getBuilderParams() {
		return builderParams;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(managerParams.hashCode())
				.append(selectorParams.hashCode())
				.append(retrieverParams.hashCode())
				.append(builderParams.hashCode())
				.build();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ModelBuildingExtraParams)) {
			return false;
		}
		ModelBuildingExtraParams o = (ModelBuildingExtraParams) obj;
		return new EqualsBuilder()
				.append(managerParams, o.managerParams)
				.append(selectorParams, o.selectorParams)
				.append(retrieverParams, o.retrieverParams)
				.append(builderParams, o.builderParams)
				.build();
	}
}
