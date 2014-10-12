package fortscale.streaming.model.prevalance.field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.samza.config.Config;
import org.apache.samza.config.ConfigException;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

public class LongValuesBucketsFieldModelTest {

	private LongValuesBucketsFieldModel createModel(List<String> buckets) {
		Config config = mock(Config.class);
		when(config.getList("fortscale.fields.myfield.buckets")).thenReturn(buckets);
		
		LongValuesBucketsFieldModel model = new LongValuesBucketsFieldModel();
		model.init("myfield", config);
		
		return model;
	}
	
	@Test
	public void long_buckets_should_give_high_score_to_event_in_non_common_buckets() {
		// initialize model
		LongValuesBucketsFieldModel model = createModel(Lists.newArrayList("10", "20"));
		for (int i=0;i<100;i++) {
			model.add(7, System.currentTimeMillis());
			model.add(13, System.currentTimeMillis());
		}
		
		// test on field on non common bucket
		double actual = model.calculateScore(21);
		
		Assert.assertEquals(100, actual, 0.01);
	}

	@Test(expected=ConfigException.class)
	public void throw_exception_when_configuration_for_buckets_is_missing() {
		createModel(Lists.newArrayList("0.5", "105.10"));
		Assert.fail();		
	}
	
}
