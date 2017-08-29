package fortscale.utils.replacement;

import org.springframework.util.Assert;

public class PatternReplacementConf {
	private String pattern;
	private String replacement;
	private String preReplacementCondition;
	private String postReplacementCondition;

	public PatternReplacementConf(String pattern, String replacement) {

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

	public void setPostReplacementCondition(String postReplacementCondition) {
		this.postReplacementCondition = postReplacementCondition;
	}

	public void setPreReplacementCondition(String preReplacementCondition) {
		this.preReplacementCondition = preReplacementCondition;
	}
}
