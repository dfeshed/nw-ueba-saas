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
	
	private DiscreetValuesCalibratedModel createModel(String ignore, Integer distinctValuesLimit, int numOfDistinctValues) {
		Config config = mock(Config.class);
		when(config.get("fortscale.fields.myfield.ignore.score.regex")).thenReturn(ignore);
		when(config.get("fortscale.fields.myfield.distinct.values.limit")).thenReturn(distinctValuesLimit == null ? null : Integer.toString(distinctValuesLimit));
		
		DiscreetValuesCalibratedModel model = new DiscreetValuesCalibratedModel();
		model.init(prefix, "myfield", config);
		
		// add to the model some values to calculate score based upon
		for (int i=0;i<numOfDistinctValues;i++){
			for(int j=0; j<100; j++){
				model.add("commonValue"+i, System.currentTimeMillis());
			}
		}
			
		
		return model;
	}
	
	@Test
	public void model_should_give_score_0_when_num_of_distinct_values_is_above_the_limit() {
		DiscreetValuesCalibratedModel model = createModel(null, 100, 100);
		
		model.add("value1", System.currentTimeMillis());
		double score = model.calculateScore("value1");
		
		Assert.assertEquals(0d, score, 0.000001);
	}
	
	@Test
	public void model_should_give_score_regular_score_when_num_of_distinct_values_is_equal_to_the_limit() {
		DiscreetValuesCalibratedModel model = createModel(null, 100, 99);
		
		model.add("value1", System.currentTimeMillis());
		double score = model.calculateScore("value1");
		
		Assert.assertEquals(99d, score, 0.000001);
	}
	
	@Test
	public void model_should_give_score_regular_score_when_limit_is_not_configured_and_num_of_distinct_values_is_equal_to_the_default_limit() {
		DiscreetValuesCalibratedModel model = createModel(null, null, DiscreetValuesCalibratedModel.DISTINCT_VALUES_LIMIT_DEFAULT-1);
		
		model.add("value1", System.currentTimeMillis());
		double score = model.calculateScore("value1");
		
		Assert.assertEquals(99d, score, 0.000001);
	}
	
	@Test
	public void model_should_give_score_0_when_limit_is_not_configured_and_num_of_distinct_values_is_above_the_default_limit() {
		DiscreetValuesCalibratedModel model = createModel(null, null, DiscreetValuesCalibratedModel.DISTINCT_VALUES_LIMIT_DEFAULT);
		
		model.add("value1", System.currentTimeMillis());
		double score = model.calculateScore("value1");
		
		Assert.assertEquals(0d, score, 0.000001);
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
