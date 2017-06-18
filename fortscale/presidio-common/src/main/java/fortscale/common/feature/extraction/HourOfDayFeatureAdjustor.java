package fortscale.common.feature.extraction;

import static fortscale.utils.ConversionUtils.convertToLong;

import java.util.Calendar;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.common.event.Event;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureValue;


@JsonTypeName(HourOfDayFeatureAdjustor.HOUR_OF_DAY_FEATURE_ADJUSTOR)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class HourOfDayFeatureAdjustor implements FeatureAdjustor {
	protected static final String HOUR_OF_DAY_FEATURE_ADJUSTOR = "hour_of_day_feature_adjustor";
	private static final int HOUR_OF_DAY_FEATURE_ADJUSTOR_TYPE_HASH_CODE = HOUR_OF_DAY_FEATURE_ADJUSTOR.hashCode();
	private static Calendar calenderHelper = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

	@Override public FeatureValue adjust(FeatureValue value, Event event) throws Exception {
		long timestamp = convertToLong(value);
		calenderHelper.setTimeInMillis(timestamp * 1000);
		return new FeatureNumericValue(new Integer(calenderHelper.get(Calendar.HOUR_OF_DAY)));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return HOUR_OF_DAY_FEATURE_ADJUSTOR_TYPE_HASH_CODE;
	}
}
