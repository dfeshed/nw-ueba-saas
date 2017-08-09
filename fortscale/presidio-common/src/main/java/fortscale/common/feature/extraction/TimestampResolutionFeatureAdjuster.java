package fortscale.common.feature.extraction;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.common.event.Event;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureValue;
import fortscale.utils.ConversionUtils;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class TimestampResolutionFeatureAdjuster implements FeatureAdjustor {
	protected static final String TIMESTAMP_RESOLUTION_FEATURE_ADJUSTER = "timestamp_resolution_feature_adjuster";

	private long resolutionInSeconds;

	@JsonCreator
	public TimestampResolutionFeatureAdjuster(@JsonProperty("resolutionInSeconds") long resolutionInSeconds) {
		Assert.isTrue(resolutionInSeconds > 0);
		this.resolutionInSeconds = resolutionInSeconds;
	}

	@Override
	public FeatureValue adjust(FeatureValue value, Event event) throws Exception {
		long epochtime = ConversionUtils.convertToLong(value);
		long adjusted = (epochtime / resolutionInSeconds) * resolutionInSeconds;
		return new FeatureNumericValue(adjusted);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TimestampResolutionFeatureAdjuster that = (TimestampResolutionFeatureAdjuster)o;
		return resolutionInSeconds == that.resolutionInSeconds;
	}

	@Override
	public int hashCode() {
		return (int)(resolutionInSeconds ^ (resolutionInSeconds >>> 32));
	}
}
