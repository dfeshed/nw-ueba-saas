package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecord;

import java.util.regex.Pattern;

public abstract class RegexScorer extends AbstractScorer {

	public static final String EMPTY_FEATURE_FIELD_NAME_ERROR_MSG = "regexFieldName must be provided and cannot be empty or blank";
	public static final String NULL_REGEX_ERROR_MSG = "regex pattern cannot be null";

	protected Pattern regexPattern;
	private String regexFieldName;
	private final FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService;

	public RegexScorer(String scorerName, String featureFieldName, Pattern pattern, FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService) {
		super(scorerName);
		Assert.isTrue(StringUtils.isNotEmpty(featureFieldName) && StringUtils.isNotBlank(featureFieldName), EMPTY_FEATURE_FIELD_NAME_ERROR_MSG);
		Assert.notNull(pattern, NULL_REGEX_ERROR_MSG);

		this.regexFieldName = featureFieldName;
		this.regexPattern = pattern;
		this.recordReaderFactoryService = recordReaderFactoryService;
	}

	protected boolean matches(AdeRecord record) {
		Object object = recordReaderFactoryService
				.getDefaultProduct(record.getAdeRecordType())
				.get(record, regexFieldName);
		Feature feature = Feature.toFeature(regexFieldName, object);
		return regexPattern.matcher(feature.getValue().toString()).matches();
	}

	public Pattern getRegexPattern() {
		return regexPattern;
	}

	public String getRegexFieldName() {
		return regexFieldName;
	}
}
