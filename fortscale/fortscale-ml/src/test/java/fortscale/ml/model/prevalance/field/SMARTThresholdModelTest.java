package fortscale.ml.model.prevalance.field;

import fortscale.ml.model.SMARTThresholdModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SMARTThresholdModelTest {
	private static class TransformationAsserter {
		private double threshold;
		private double maxSeenScore;

		public TransformationAsserter setThreshold(double threshold) {
			this.threshold = threshold;
			return this;
		}

		public TransformationAsserter setMaxSeenScore(double maxSeenScore) {
			this.maxSeenScore = maxSeenScore;
			return this;
		}

		private SMARTThresholdModel createModel() {
			SMARTThresholdModel model = new SMARTThresholdModel();
			model.init(threshold, maxSeenScore);
			return model;
		}

		public void doAssertTransform(double expectedTransformedScore, double score) {
			SMARTThresholdModel model = createModel();
			double transformedScore = model.transformScore(score);
			Assert.assertEquals(expectedTransformedScore, transformedScore, 0.0001);
		}

		public void doAssertTransformAndRestore(double expectedTransformedScore, double score) {
			SMARTThresholdModel model = createModel();
			double transformedScore = model.transformScore(score);
			Assert.assertEquals(expectedTransformedScore, transformedScore, 0.0001);
			Assert.assertEquals(score, model.restoreOriginalScore(transformedScore), 0.0001);
		}

		public void doAssertTransformedLessThan(double score, double val) {
			SMARTThresholdModel model = createModel();
			Assert.assertTrue(model.transformScore(score) < val);
		}

		public void doAssertTransformedGreaterOrEqualThan(double score, double val) {
			SMARTThresholdModel model = createModel();
			Assert.assertTrue(model.transformScore(score) >= val);
		}

		public void doAssertRestoredLessThan(double score, double val) {
			SMARTThresholdModel model = createModel();
			Assert.assertTrue(model.restoreOriginalScore(score) < val);
		}

		public void doAssertRestoredGreaterOrEqualThan(double score, double val) {
			SMARTThresholdModel model = createModel();
			Assert.assertTrue(model.restoreOriginalScore(score) >= val);
		}
	}

	@Test
	public void shouldNotTransformScoreIfThresholdIs50AndMaxSeenScoreIs100() {
		for (int score = 0; score <= 100; score++) {
			new TransformationAsserter().setThreshold(50).setMaxSeenScore(100).doAssertTransformAndRestore(score, score);
		}
	}

	@Test
	public void shouldTransformThresholdInto50() {
		int threshold = 90;
		new TransformationAsserter().setThreshold(threshold).setMaxSeenScore(100).doAssertTransformAndRestore(50, threshold);
	}

	@Test
	public void shouldTransformMaxSeenScoreInto100() {
		int maxSeenScore = 95;
		new TransformationAsserter().setThreshold(90).setMaxSeenScore(maxSeenScore).doAssertTransformAndRestore(100, maxSeenScore);
	}

	@Test
	public void shouldTransformGreaterThenMaxSeenScoreInto100() {
		int maxSeenScore = 95;
		new TransformationAsserter().setThreshold(90).setMaxSeenScore(maxSeenScore).doAssertTransform(100, 100);
	}

	@Test
	public void shouldTransformScoresGreaterThanThresholdToScoresGreaterThan50() {
		int threshold = 90;
		for (int score = 0; score < threshold; score++) {
			new TransformationAsserter().setThreshold(threshold).setMaxSeenScore(100).doAssertTransformedLessThan(score, 50);
		}
		for (int score = threshold; score <= 100; score++) {
			new TransformationAsserter().setThreshold(threshold).setMaxSeenScore(100).doAssertTransformedGreaterOrEqualThan(score, 50);
		}
	}

	@Test
	public void shouldRestoreScoresGreaterThan50ToScoresGreaterThanThreshold() {
		int threshold = 90;
		for (int score = 0; score < 50; score++) {
			new TransformationAsserter().setThreshold(threshold).setMaxSeenScore(100).doAssertRestoredLessThan(score, threshold);
		}
		for (int score = 50; score <= 100; score++) {
			new TransformationAsserter().setThreshold(threshold).setMaxSeenScore(100).doAssertRestoredGreaterOrEqualThan(score, threshold);
		}
	}
}
