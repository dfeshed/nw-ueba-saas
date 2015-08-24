package fortscale.streaming.feature.extractor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

@JsonTypeName(ConditionalPatternReplacementFeatureAdjuster.CONDITIONAL_PATTERN_REPLACEMENT_FEATURE_ADJUSTER_TYPE)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ConditionalPatternReplacementFeatureAdjuster implements FeatureAdjustor {
	protected static final String CONDITIONAL_PATTERN_REPLACEMENT_FEATURE_ADJUSTER_TYPE = "conditional_pattern_replacement_feature_adjuster";

	private String pattern;
	private String replacement;
	private String preReplacementCondition;
	private String postReplacementCondition;

	public ConditionalPatternReplacementFeatureAdjuster(
			@JsonProperty("pattern") String pattern,
			@JsonProperty("replacement") String replacement,
			@JsonProperty("preReplacementCondition") String preReplacementCondition,
			@JsonProperty("postReplacementCondition") String postReplacementCondition) {

		setPattern(pattern);
		setReplacement(replacement);
		setPreReplacementCondition(preReplacementCondition);
		setPostReplacementCondition(postReplacementCondition);
	}

	public void setPattern(String pattern) {
		Assert.isTrue(StringUtils.isNotEmpty(pattern), "Illegal empty pattern");
		this.pattern = pattern;
	}

	public void setReplacement(String replacement) {
		Assert.notNull(replacement, "Illegal null replacement");
		this.replacement = replacement;
	}

	public void setPreReplacementCondition(String preReplacementCondition) {
		// If there is no condition, preReplacementCondition can be null
		this.preReplacementCondition = preReplacementCondition;
	}

	public void setPostReplacementCondition(String postReplacementCondition) {
		// If there is no condition, postReplacementCondition can be null
		this.postReplacementCondition = postReplacementCondition;
	}

	@Override
	public Object adjust(Object feature, JSONObject message) {
		String originalFieldValue = ConversionUtils.convertToString(feature);

		// If original field value is null, normalized field value should also be null
		if (originalFieldValue == null) {
			return null;
		}

		// If original field value does not match pre condition, it should not be replaced
		if (preReplacementCondition != null && !originalFieldValue.matches(preReplacementCondition)) {
			return originalFieldValue;
		}

		// Replace all occurrences of the pattern in the original field value
		String normalizedFieldValue = originalFieldValue.replaceAll(pattern, replacement);

		// If normalized field value does not match post condition, return original one
		if (postReplacementCondition != null && !normalizedFieldValue.matches(postReplacementCondition)) {
			return originalFieldValue;
		}

		// If normalized field value matches post condition, return it
		return normalizedFieldValue;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConditionalPatternReplacementFeatureAdjuster that = (ConditionalPatternReplacementFeatureAdjuster)o;
		if (!pattern.equals(that.pattern)) return false;
		if (!replacement.equals(that.replacement)) return false;
		if (preReplacementCondition != null ? !preReplacementCondition.equals(that.preReplacementCondition) : that.preReplacementCondition != null)
			return false;
		if (postReplacementCondition != null ? !postReplacementCondition.equals(that.postReplacementCondition) : that.postReplacementCondition != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = pattern.hashCode();
		result = 31 * result + replacement.hashCode();
		result = 31 * result + (preReplacementCondition != null ? preReplacementCondition.hashCode() : 0);
		result = 31 * result + (postReplacementCondition != null ? postReplacementCondition.hashCode() : 0);
		return result;
	}
}
