package fortscale.ml.model.prevalance.field;

import static org.mockito.Mockito.*;

import org.apache.samza.config.Config;
import org.junit.Assert;
import org.junit.Test;

public class DiscreetValuesCalibratedModelTest {
	
	private static final String prefix = "fortscale.fields";

	private DiscreetValuesCalibratedModel createModel(String ignore) {
		Config config = mock(Config.class);
		when(config.get("fortscale.fields.myfield.ignore.score.regex")).thenReturn(ignore);
		
		DiscreetValuesCalibratedModel model = new DiscreetValuesCalibratedModel();
		model.init(prefix, "myfield", config);
		
		// add to the model some values to calculate score based upon
		for (int i=0;i<1000;i++)
			model.add("commonValue", System.currentTimeMillis());
		
		return model;
	}
	
	@Test
	public void model_should_give_score_0_to_regex_values_in_the_ignore() {
		DiscreetValuesCalibratedModel model = createModel("value\\d");
		
		model.add("value1", System.currentTimeMillis());
		double score = model.calculateScore("value1");
		
		Assert.assertEquals(0d, score, 0.000001);
	}

	@Test
	public void model_should_give_high_score_to_values_not_in_ignore_list() {
		DiscreetValuesCalibratedModel model = createModel("");
		
		model.add("value1", System.currentTimeMillis());
		double score = model.calculateScore("value1");
		
		Assert.assertTrue(score > 0);
	}
		
	@Test
	public void model_should_give_score_0_to_empty_string() {
		DiscreetValuesCalibratedModel model = createModel("");
		
		model.add("", System.currentTimeMillis());
		double score = model.calculateScore("");
				
		Assert.assertEquals(0d, score, 0.000001);
	}
}
