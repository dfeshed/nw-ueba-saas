package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

public abstract class RegexScorer extends AbstractScorer {

	public static final String EMPTY_FEATURE_FIELD_NAME_ERROR_MSG = "regexFieldName must be provided and cannot be empty or blank";
	public static final String NULL_REGEX_ERROR_MSG = "regex pattern cannot be null";

	protected Pattern regexPattern;
	private String regexFieldName;

	public RegexScorer(String scorerName, String featureFieldName, Pattern pattern){
		super(scorerName);
		Assert.isTrue(StringUtils.isNotEmpty(featureFieldName) && StringUtils.isNotBlank(featureFieldName), EMPTY_FEATURE_FIELD_NAME_ERROR_MSG);
		Assert.notNull(pattern, NULL_REGEX_ERROR_MSG);

		this.regexFieldName = featureFieldName;
		this.regexPattern = pattern;
	}
	
	protected boolean matches(Event eventMessage){
		Assert.notNull(eventMessage);
		Feature feature = featureExtractService.extract(regexFieldName, eventMessage);
		return regexPattern.matcher(feature.getValue().toString()).matches();
	}

	public Pattern getRegexPattern() {
		return regexPattern;
	}

	public String getRegexFieldName() {
		return regexFieldName;
	}
}
