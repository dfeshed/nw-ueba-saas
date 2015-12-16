package fortscale.ml.model.builder;

import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.ml.model.Model;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

public class TimeModelBuilderTest {
	@Test
	public void shouldBuildModelWithGivenTimeResolutionAndBucketSize() {
		Integer timeResolution = 60;
		Integer bucketSize = 10;
		TimeModelBuilder builder = new TimeModelBuilder(timeResolution, bucketSize);
		Model model = builder.build(new GenericHistogram());
		Assert.assertEquals(timeResolution, Whitebox.getInternalState(model, "timeResolution"));
		Assert.assertEquals(bucketSize, Whitebox.getInternalState(model, "bucketSize"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsTimeResolution() {
		new TimeModelBuilder(null, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsBucketSize() {
		new TimeModelBuilder(1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsTimeResolution() {
		new TimeModelBuilder(-1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNegativeAsBucketSize() {
		new TimeModelBuilder(1, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInputToBuild() {
		new TimeModelBuilder(1, 1).build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputTypeToBuild() {
		new TimeModelBuilder(1, 1).build("");
	}
}
