package fortscale.streaming.feature.extractor;

import static fortscale.utils.ConversionUtils.convertToString;

import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonTypeName;

import net.minidev.json.JSONObject;

@JsonTypeName(PatternReplacementFeatureAdjustor.PATTERN_REPLACEMENT_FEATURE_ADJUSTOR_TYPE)
public class PatternReplacementFeatureAdjustor implements FeatureAdjustor {
	protected static final String PATTERN_REPLACEMENT_FEATURE_ADJUSTOR_TYPE = "pattern_replacment_feature_adjustor";
	
	private String pattern;
	private String replacement;
	
	
	public PatternReplacementFeatureAdjustor(){}
	
	public PatternReplacementFeatureAdjustor(String pattern, String replacement) {
		this.pattern = pattern;
		this.replacement = replacement;
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

		PatternReplacementFeatureAdjustor that = (PatternReplacementFeatureAdjustor) o;

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
