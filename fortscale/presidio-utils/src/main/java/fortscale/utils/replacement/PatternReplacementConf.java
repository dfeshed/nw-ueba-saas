package fortscale.utils.replacement;

import org.springframework.util.Assert;

public class PatternReplacementConf {
	private String pattern;
	private String replacement;
	private String preReplacementCondition;
	private String postReplacementCondition;

	public PatternReplacementConf(String pattern, String replacement, String preReplacementCondition, String postReplacementCondition) {

		this.pattern = pattern;
		this.replacement = replacement;
		this.preReplacementCondition = preReplacementCondition;
		this.postReplacementCondition = postReplacementCondition;
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
