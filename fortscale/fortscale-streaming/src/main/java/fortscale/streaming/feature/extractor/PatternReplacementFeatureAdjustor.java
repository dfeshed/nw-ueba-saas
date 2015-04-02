package fortscale.streaming.feature.extractor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;

import static fortscale.utils.ConversionUtils.convertToString;

@JsonTypeName(PatternReplacementFeatureAdjustor.PATTERN_REPLACEMENT_FEATURE_ADJUSTOR_TYPE)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class PatternReplacementFeatureAdjustor implements FeatureAdjustor {
	protected static final String PATTERN_REPLACEMENT_FEATURE_ADJUSTOR_TYPE = "pattern_replacment_feature_adjustor";

	private String pattern;
	private String replacement;

	public PatternReplacementFeatureAdjustor(@JsonProperty("pattern") String pattern, @JsonProperty("replacement") String replacement) {
		setPattern(pattern);
		setReplacement(replacement);
	}

	@Override
	public Object adjust(Object feature, JSONObject message) {
		String originalFieldValue = convertToString(feature);
		if (StringUtils.isNotEmpty(originalFieldValue) && StringUtils.isNotEmpty(pattern)) {
			// strip numbers from the hostname
			originalFieldValue = originalFieldValue.replaceAll(Matcher.quoteReplacement(pattern), Matcher.quoteReplacement(replacement));
		}

		return originalFieldValue;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		PatternReplacementFeatureAdjustor that = (PatternReplacementFeatureAdjustor)o;
		if (!pattern.equals(that.pattern))
			return false;
		if (!replacement.equals(that.replacement))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return pattern.hashCode();
	}
}
