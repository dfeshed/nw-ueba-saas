package fortscale.aggregation.feature.extraction;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.utils.logging.Logger;

@JsonTypeName(EventFeatureExtractor.EVENT_FEATURE_EXTRACTOR_TYPE)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class EventFeatureExtractor implements FeatureExtractor {
	private static final Logger logger = Logger.getLogger(EventFeatureExtractor.class);
	protected static final String EVENT_FEATURE_EXTRACTOR_TYPE = "event_feature_extractor";


	private String fieldName;
	private FeatureAdjustor featureAdjustor;

	public EventFeatureExtractor(
			@JsonProperty("fieldName") String fieldName,
			@JsonProperty("featureAdjustor") FeatureAdjustor featureAdjustor) {
		setFieldName(fieldName);
		setFeatureAdjustor(featureAdjustor);
	}

	@Override
	public Object extract(Event event) {
		Object ret = null;

		try {
			ret = extractValue(event);
		} catch (Exception e) {
			logger.debug("got the following exception while trying to extract feature", e);
		}

		return ret;
	}

	protected Object extractValue(Event event) throws Exception {
		Object value = event.get(fieldName);


		if (featureAdjustor != null) {
			return featureAdjustor.adjust(value, event);
		} else {
			return value;
		}
	}


	public String getFieldName() {
		return fieldName;
	}

	private void setFieldName(String fieldName) {
		Assert.isTrue(StringUtils.isNotBlank(fieldName), "Illegal blank field name");
		this.fieldName = fieldName;
	}


	private void setFeatureAdjustor(FeatureAdjustor featureAdjustor) {
		this.featureAdjustor = featureAdjustor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		EventFeatureExtractor that = (EventFeatureExtractor)o;
		if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null)
			return false;
		if (!featureAdjustor.equals(that.featureAdjustor)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return fieldName.hashCode();
	}
}
