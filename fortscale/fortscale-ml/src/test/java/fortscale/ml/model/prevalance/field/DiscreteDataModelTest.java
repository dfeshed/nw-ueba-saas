package fortscale.ml.model.prevalance.field;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.regex.Pattern;

public class DiscreteDataModelTest {
	private DiscreteDataModel createModel(String ignoreValues, Double... counts) throws Exception {
		Pattern ignorePattern = null;
		if (ignoreValues != null) {
			ignorePattern = Pattern.compile(ignoreValues);
		}
		DiscreteDataModel model = new DiscreteDataModel(ignorePattern);
		model.setFeatureCounts(Arrays.asList(counts));
		return model;
	}

	@Test
	public void shouldScore0EmptyString() throws Exception {
		DiscreteDataModel model = createModel(null, 1d, 100d);

		double score = model.calculateScore(new ImmutablePair<Object, Double>("", 1d));
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void shouldScore0ToIgnoredValues() throws Exception {
		final String ignore = "ignore";
		DiscreteDataModel model = createModel(ignore, 1d, 100d);

		double score = model.calculateScore(new ImmutablePair<Object, Double>(ignore, 1d));
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void shouldScorePositiveToNotIgnoredValues() throws Exception {
		DiscreteDataModel model = createModel("ignore", 1d, 100d);

		double score = model.calculateScore(new ImmutablePair<Object, Double>("do not ignore", 1d));
		Assert.assertTrue(score > 0);
	}

	@Test
	public void shouldGetNullFeatureValueForNull() throws Exception {
		DiscreteDataModel model = new DiscreteDataModel(null);
		Assert.assertNull(model.getFeatureValue(null));
	}

	@Test
	public void shouldGetNullFeatureValueForEmptyString() throws Exception {
		DiscreteDataModel model = new DiscreteDataModel(null);
		Assert.assertNull(model.getFeatureValue(""));
	}

	@Test
	public void shouldGetStringRepresentationAsFeatureValue() throws Exception {
		final String featureValue = "string representation";
		DiscreteDataModel model = new DiscreteDataModel(null);
		Object value = new Object() {
			@Override
			public String toString() {
				return featureValue;
			}
		};
		Assert.assertEquals(featureValue, model.getFeatureValue(value));
	}

	@Test
	public void shouldGetNullFeatureValueForIgnoredValue() throws Exception {
		String ignore = "ignore";
		DiscreteDataModel model = new DiscreteDataModel(Pattern.compile(ignore));
		Assert.assertNull(model.getFeatureValue(ignore));
	}
}
