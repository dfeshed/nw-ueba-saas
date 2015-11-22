package fortscale.ml.model.prevalance.field;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.regex.Pattern;

public class DiscreteDataModelTest {
	@Test
	public void shouldScore0EmptyString() throws Exception {
		final DiscreteDataModel model = new DiscreteDataModel(null);
		final String emptyString = "";
		model.setFeatureCounts(new HashMap<String, Double>() {{
			this.put(emptyString, 1d);
		}});

		double score = model.calculateScore(new ImmutablePair<Object, Double>(emptyString, 1d));
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void shouldScore0ToIgnoredValues() throws Exception {
		final String ignore = "ignore";
		final DiscreteDataModel model = new DiscreteDataModel(Pattern.compile(ignore));
		model.setFeatureCounts(new HashMap<String, Double>() {{
			this.put(ignore, 1d);
		}});

		double score = model.calculateScore(new ImmutablePair<Object, Double>(ignore, 1d));
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void shouldScorePositiveToNotIgnoredValues() throws Exception {
		final String ignore = "ignore";
		final String s = "do not ignore";
		final DiscreteDataModel model = new DiscreteDataModel(Pattern.compile(ignore));
		model.setFeatureCounts(new HashMap<String, Double>() {{
			this.put(ignore, 1d);
			this.put(s, 1d);
			this.put("frequent value", 100d);
		}});

		double score = model.calculateScore(new ImmutablePair<Object, Double>(s, 1d));
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
