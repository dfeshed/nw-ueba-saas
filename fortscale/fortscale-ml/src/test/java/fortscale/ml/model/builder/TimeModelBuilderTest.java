package fortscale.ml.model.builder;

import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.ml.model.Model;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

public class TimeModelBuilderTest {
	private static TimeModelBuilderConf getConfig(int timeResolution, int bucketSize, int minEvents, int maxRareTimestampCount, int maxNumOfRareTimestamps) {
		return new TimeModelBuilderConf(timeResolution, bucketSize, minEvents, maxRareTimestampCount, maxNumOfRareTimestamps);
	}

	@Test
	public void shouldBuildModelWithGivenTimeResolutionAndBucketSize() {
		Integer timeResolution = 60;
		Integer bucketSize = 10;
		int minEvents = 1;
		int maxRareTimestampCount = 10;
		int maxNumOfRareTimestamps = 6;
		TimeModelBuilder builder = new TimeModelBuilder(getConfig(timeResolution, bucketSize, minEvents, maxRareTimestampCount, maxNumOfRareTimestamps));
		Model model = builder.build(new GenericHistogram());
		Assert.assertEquals(timeResolution, Whitebox.getInternalState(model, "timeResolution"));
		Assert.assertEquals(bucketSize, Whitebox.getInternalState(model, "bucketSize"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsTimeResolution() {
		new TimeModelBuilder(getConfig(-1, 1, 1, 1, 1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsBucketSize() {
		new TimeModelBuilder(getConfig(1, -1, 1, 1, 1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsMinEvents() {
		new TimeModelBuilder(getConfig(1, 1, -1, 1, 1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsMaxRareTimestampCount() {
		new TimeModelBuilder(getConfig(1, 1, 1, -1, 1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsMaxNumOfRareTimestamps() {
		new TimeModelBuilder(getConfig(1, 1, 1, 1, -1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInputToBuild() {
		new TimeModelBuilder(getConfig(1, 1, 1, 1, 1)).build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputTypeToBuild() {
		new TimeModelBuilder(getConfig(1, 1, 1, 1, 1)).build("");
	}
}
