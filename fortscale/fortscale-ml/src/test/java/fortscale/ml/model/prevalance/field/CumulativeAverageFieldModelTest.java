package fortscale.ml.model.prevalance.field;

import org.junit.Assert;
import org.junit.Test;

public class CumulativeAverageFieldModelTest {
	
	private static final double DELTA = 1e-15;
	
	@Test
	public void score_for_model_with_one_event_should_return_the_field_value() {
		CumulativeAverageFieldModel subject = new CumulativeAverageFieldModel();
		subject.add(100, System.currentTimeMillis());
		
		double score = subject.calculateScore(100);
		Assert.assertEquals(100, score, DELTA);
	}
	
	@Test
	public void score_for_model_with_no_events_should_return_zero() {
		CumulativeAverageFieldModel subject = new CumulativeAverageFieldModel();
		
		double score = subject.calculateScore(100);
		Assert.assertEquals(0, score, DELTA);
	}
	
	@Test
	public void score_with_null_as_field_value_should_do_nothing_and_return_zero() {
		CumulativeAverageFieldModel subject = new CumulativeAverageFieldModel();
		subject.add(null, System.currentTimeMillis());
		
		double score = subject.calculateScore(100);
		Assert.assertEquals(0, score, DELTA);
	}
	
	@Test
	public void score_should_return_the_average_of_all_items() {
		CumulativeAverageFieldModel subject = new CumulativeAverageFieldModel();
		subject.add(100, System.currentTimeMillis());
		subject.add(200, System.currentTimeMillis());
		
		double score = subject.calculateScore(100);
		Assert.assertEquals(150, score, DELTA);
	}
}
