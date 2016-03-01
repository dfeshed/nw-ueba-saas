package fortscale.ml.model.retriever.pattern.replacement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class PatternReplacementConf {
	private String pattern;
	private String replacement;
	private String preReplacementCondition;
	private String postReplacementCondition;

	@JsonCreator
	public PatternReplacementConf(
			@JsonProperty("pattern") String pattern,
			@JsonProperty("replacement") String replacement) {

		Assert.hasLength(pattern, "Pattern cannot be empty or null.");
		Assert.notNull(replacement, "Replacement cannot be null.");
		this.pattern = pattern;
		this.replacement = replacement;
		this.preReplacementCondition = null;
		this.postReplacementCondition = null;
	}

	public String getPattern() {
		return pattern;
	}

	public String getReplacement() {
		return replacement;
	}

	public String getPreReplacementCondition() {
		return preReplacementCondition;
	}

	public String getPostReplacementCondition() {
		return postReplacementCondition;
	}
}
