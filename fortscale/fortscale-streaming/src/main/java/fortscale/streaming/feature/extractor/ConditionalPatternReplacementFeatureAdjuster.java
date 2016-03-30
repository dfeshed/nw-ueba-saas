package fortscale.streaming.feature.extractor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.ml.model.retriever.pattern.replacement.PatternReplacement;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.util.Assert;

@JsonTypeName(ConditionalPatternReplacementFeatureAdjuster.CONDITIONAL_PATTERN_REPLACEMENT_FEATURE_ADJUSTER_TYPE)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ConditionalPatternReplacementFeatureAdjuster implements FeatureAdjustor {
	protected static final String CONDITIONAL_PATTERN_REPLACEMENT_FEATURE_ADJUSTER_TYPE =
			"conditional_pattern_replacement_feature_adjuster";

	private String pattern;
	private String replacement;
	private String preReplacementCondition;
	private String postReplacementCondition;

	@JsonCreator
	public ConditionalPatternReplacementFeatureAdjuster(
			@JsonProperty("pattern") String pattern,
			@JsonProperty("replacement") String replacement) {

		setPattern(pattern);
		setReplacement(replacement);
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
		return PatternReplacement.replacePattern(
				originalFieldValue,
				pattern,
				replacement,
				preReplacementCondition,
				postReplacementCondition);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConditionalPatternReplacementFeatureAdjuster that = (ConditionalPatternReplacementFeatureAdjuster)o;
		return new EqualsBuilder()
				.append(this.pattern, that.pattern)
				.append(this.replacement, that.replacement)
				.append(this.preReplacementCondition, that.preReplacementCondition)
				.append(this.postReplacementCondition, that.postReplacementCondition)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return pattern.hashCode();
	}
}
