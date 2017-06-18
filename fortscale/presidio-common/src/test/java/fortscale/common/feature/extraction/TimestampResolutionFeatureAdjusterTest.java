package fortscale.common.feature.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.event.Event;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureValue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TimestampResolutionFeatureAdjusterTest {
	@Test
	public void serialize_to_json() throws Exception {
		TimestampResolutionFeatureAdjuster adjuster = new TimestampResolutionFeatureAdjuster(60);
		String expected = "{\"type\":\"timestamp_resolution_feature_adjuster\",\"resolutionInSeconds\":60}";
		String actual = (new ObjectMapper()).writeValueAsString(adjuster);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void deserialize_from_json() throws Exception {
		String json = "{\"type\":\"timestamp_resolution_feature_adjuster\",\"resolutionInSeconds\":3600}";
		Assert.assertEquals(
				new TimestampResolutionFeatureAdjuster(3600),
				(new ObjectMapper()).readValue(json, FeatureAdjustor.class));
	}

	@Test(expected = Exception.class)
	public void deserialization_should_fail_when_resolution_in_seconds_is_null() throws Exception {
		String json = "{\"type\":\"timestamp_resolution_feature_adjuster\",\"resolutionInSeconds\":null}";
		(new ObjectMapper()).readValue(json, FeatureAdjustor.class);
	}

	@Test(expected = Exception.class)
	public void deserialization_should_fail_when_resolution_in_seconds_is_not_positive() throws Exception {
		String json = "{\"type\":\"timestamp_resolution_feature_adjuster\",\"resolutionInSeconds\":0}";
		(new ObjectMapper()).readValue(json, FeatureAdjustor.class);
	}

	@Test
	public void adjuster_should_adjust_values_correctly() throws Exception {
		Event event = Mockito.mock(Event.class);

		// 1 minute resolution
		TimestampResolutionFeatureAdjuster adjuster = new TimestampResolutionFeatureAdjuster(60);
		long expected = getLongValue(new FeatureNumericValue(1420070400));
		long actual = getLongValue(adjuster.adjust(new FeatureNumericValue(1420070430), event));
		Assert.assertEquals(expected, actual);

		// 2 minute resolution
		adjuster = new TimestampResolutionFeatureAdjuster(120);
		expected = getLongValue(new FeatureNumericValue(1420122600));
		actual = getLongValue(adjuster.adjust(new FeatureNumericValue(1420122675), event));
		Assert.assertEquals(expected, actual);

		// 1 hour resolution
		adjuster = new TimestampResolutionFeatureAdjuster(3600);
		expected = getLongValue(new FeatureNumericValue(1420106400));
		actual = getLongValue(adjuster.adjust(new FeatureNumericValue(1420109100), event));
		Assert.assertEquals(expected, actual);

		// 1 day resolution
		adjuster = new TimestampResolutionFeatureAdjuster(86400);
		expected = getLongValue(new FeatureNumericValue(1433548800));
		actual = getLongValue(adjuster.adjust(new FeatureNumericValue(1433596393), event));
		Assert.assertEquals(expected, actual);

		// 1 week resolution
		adjuster = new TimestampResolutionFeatureAdjuster(604800);
		expected = getLongValue(new FeatureNumericValue(1436400000));
		actual = getLongValue(adjuster.adjust(new FeatureNumericValue(1437000307), event));
		Assert.assertEquals(expected, actual);
	}

	private static long getLongValue(FeatureValue value) {
		return ((FeatureNumericValue)value).getValue().longValue();
	}
}
