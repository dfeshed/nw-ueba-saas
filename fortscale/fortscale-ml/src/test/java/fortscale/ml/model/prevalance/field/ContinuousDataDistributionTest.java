package fortscale.ml.model.prevalance.field;

import org.apache.samza.config.Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContinuousDataDistributionTest {
	private static final String PREFIX = "prefix";
	private static final String FIELD_NAME = "fieldName";
	private static final int NUM_OF_DISTINCT_VALUES = 10;
	private static final double BUCKET_SIZE = 2.0;

	private Config config;

	@Before
	public void setUp() throws Exception {
		config = mock(Config.class);
	}

	private ContinuousDataDistribution create() {
		ContinuousDataDistribution distribution = new ContinuousDataDistribution();

		when(config.getInt(eq(String.format("%s.%s.continuous.data.distribution.min.distinct.values", PREFIX, FIELD_NAME)), anyInt())).thenReturn(NUM_OF_DISTINCT_VALUES);
		when(config.getInt(eq(String.format("%s.%s.continuous.data.distribution.max.distinct.values", PREFIX, FIELD_NAME)), anyInt())).thenReturn(NUM_OF_DISTINCT_VALUES);
		when(config.getDouble(eq(String.format("%s.%s.continuous.data.distribution.min.bucket.size", PREFIX, FIELD_NAME)), anyDouble())).thenReturn(BUCKET_SIZE);
		when(config.getDouble(eq(String.format("%s.%s.continuous.data.distribution.max.bucket.size", PREFIX, FIELD_NAME)), anyDouble())).thenReturn(BUCKET_SIZE);

		distribution.init(PREFIX, FIELD_NAME, config);
		return distribution;
	}

	@Test
	public void uniformDistributionWithOneUpOutliersTest() {
		ContinuousDataDistribution distribution = create();
		final int separator = ContinuousDataModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY;

		double startVal = 1000000;
		for (int i = 0; i < 1000; i++) {
			double val = startVal + i;
			distribution.add(val, 0);
		}

		double outlierVal = startVal + 1100;
		distribution.add(outlierVal, 0);
		Assert.assertEquals(separator + 0.04161, distribution.calculateScore(outlierVal), 0.00001);

		outlierVal = startVal + 1200;
		distribution.add(outlierVal, 0);
		Assert.assertEquals(separator + 0.01820, distribution.calculateScore(outlierVal), 0.00001);

		outlierVal = startVal + 1300;
		distribution.add(outlierVal, 0);
		Assert.assertEquals(separator + 0.01404, distribution.calculateScore(outlierVal), 0.00001);

		outlierVal = startVal + 1400;
		distribution.add(outlierVal, 0);
		Assert.assertEquals(separator + 0.00540, distribution.calculateScore(outlierVal), 0.00001);

		outlierVal = startVal + 1500;
		distribution.add(outlierVal, 0);
		Assert.assertEquals(separator + 0.00186, distribution.calculateScore(outlierVal), 0.00001);
		Assert.assertEquals(-separator - 0.04095, distribution.calculateScore(startVal), 0.00001);
		Assert.assertEquals(-separator - 0.37778, distribution.calculateScore(startVal + 500), 0.00001);
	}
}
