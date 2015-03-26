package fortscale.streaming.feature.extractor;

import static fortscale.utils.ConversionUtils.convertToString;

import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;

import net.minidev.json.JSONObject;

public class PatternReplacementFeatureExtractor extends MessageFeatureExtractor {

	private String pattern;
	private String replacement;
	
	
	public PatternReplacementFeatureExtractor(){}
	
	public PatternReplacementFeatureExtractor(String originalFieldName, String normalizedFieldName, String pattern, String replacement) {
		super(originalFieldName, normalizedFieldName);
		this.pattern = pattern;
		this.replacement = replacement;
	}

	@Override
	protected Object extractValue(JSONObject message) {
		String originalFieldValue = convertToString(message.get(originalFieldName));
		if (StringUtils.isNotEmpty(originalFieldValue) && StringUtils.isNotEmpty(pattern)) {
			// strip numbers from the hostname
			originalFieldValue = originalFieldValue.replaceAll(Matcher.quoteReplacement(pattern), Matcher.quoteReplacement(replacement));
		}
		
		return originalFieldValue;
	}
}
