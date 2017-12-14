package fortscale.ml.model.builder;

import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.TimeModel;
import fortscale.ml.model.metrics.TimeModelBuilderMetricsContainer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static org.mockito.Mockito.mock;

public class TimeModelBuilderTest {

	private TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer = mock(TimeModelBuilderMetricsContainer.class);

	private static TimeModelBuilderConf getConfig(int timeResolution, int bucketSize, int maxRareTimestampCount) {
		return new TimeModelBuilderConf(timeResolution, bucketSize, maxRareTimestampCount);
	}

	@Test
	public void shouldBuildModelWithGivenParameters() {
		Integer timeResolution = 60;
		Integer bucketSize = 10;
		Integer maxRareTimestampCount = 15;
		TimeModelBuilder builder = new TimeModelBuilder(getConfig(timeResolution, bucketSize, maxRareTimestampCount), timeModelBuilderMetricsContainer);
		TimeModel model = (TimeModel) builder.build(new GenericHistogram());
		Assert.assertEquals(timeResolution, Whitebox.getInternalState(model, "timeResolution"));
		Assert.assertEquals(bucketSize, Whitebox.getInternalState(model, "bucketSize"));
		Assert.assertEquals(maxRareTimestampCount * 2, model.getCategoryRarityModel().getBuckets().length);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsTimeResolution() {
		new TimeModelBuilder(getConfig(-1, 1, 15), timeModelBuilderMetricsContainer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsBucketSize() {
		new TimeModelBuilder(getConfig(1, -1, 15), timeModelBuilderMetricsContainer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsMaxRareTimestampCount() {
		new TimeModelBuilder(getConfig(1, 1, -1), timeModelBuilderMetricsContainer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInputToBuild() {
		new TimeModelBuilder(getConfig(1, 1, 15), timeModelBuilderMetricsContainer).build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputTypeToBuild() {
		new TimeModelBuilder(getConfig(1, 1, 15), timeModelBuilderMetricsContainer).build("");
	}
}
