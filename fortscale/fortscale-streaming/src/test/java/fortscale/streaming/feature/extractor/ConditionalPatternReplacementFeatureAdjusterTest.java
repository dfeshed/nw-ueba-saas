package fortscale.streaming.feature.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class ConditionalPatternReplacementFeatureAdjusterTest {
	private static final String FEATURE_EXTRACTOR_FORMAT = "{\"type\":\"event_feature_extractor\",\"originalFieldName\":\"%s\",\"normalizedFieldName\":\"%s\",\"featureAdjustor\":%s}";
	private static final String DEFAULT_ORIGINAL_FIELD_NAME = "before";
	private static final String DEFAULT_NORMALIZED_FIELD_NAME = "after";

	@Test
	public void feature_extractor_should_be_deserialized_from_string_with_no_conditions() throws Exception {
		String featureAdjuster = "{\"type\":\"conditional_pattern_replacement_feature_adjuster\",\"pattern\":\"fortscale\",\"replacement\":\"FS\"}";
		String featureExtractor = String.format(FEATURE_EXTRACTOR_FORMAT, DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);

		FeatureExtractor actualFeatureExtractor = (new ObjectMapper()).readValue(featureExtractor, FeatureExtractor.class);
		Assert.assertNotNull(actualFeatureExtractor);

		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "fortscale_user");

		String expected = "FS_user";
		Object actual = actualFeatureExtractor.extract(message);
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected, message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_should_be_deserialized_from_string_with_conditions() throws Exception {
		String featureAdjusterFormat = "{\"type\":\"conditional_pattern_replacement_feature_adjuster\",\"pattern\":\"%s\",\"replacement\":\"%s\",\"preReplacementCondition\":\"%s\",\"postReplacementCondition\":\"%s\"}";
		String featureAdjuster = String.format(featureAdjusterFormat, "-", "_", "FS.*", ".*[0-9]+");
		String featureExtractor = String.format(FEATURE_EXTRACTOR_FORMAT, DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);

		FeatureExtractor actualFeatureExtractor = (new ObjectMapper()).readValue(featureExtractor, FeatureExtractor.class);
		Assert.assertNotNull(actualFeatureExtractor);

		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "FS-user10");

		String expected = "FS_user10";
		Object actual = actualFeatureExtractor.extract(message);
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected, message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_should_find_the_pattern_and_replace_it() {
		ConditionalPatternReplacementFeatureAdjuster featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("\\d", "0");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "a1b2c3d4e5");
		featureExtractor.extract(message);
		Assert.assertEquals("a0b0c0d0e0", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_should_not_find_the_pattern_and_should_not_make_any_changes() {
		ConditionalPatternReplacementFeatureAdjuster featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("[a-z]{2}$", "-");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "endOfLINE");
		featureExtractor.extract(message);
		Assert.assertEquals("endOfLINE", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_true_pre_condition_should_find_the_pattern_and_replace_it() {
		ConditionalPatternReplacementFeatureAdjuster featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("fortscale", "FS");
		featureAdjuster.setPreReplacementCondition(".+@.+\\.com");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "test@fortscale.com");
		featureExtractor.extract(message);
		Assert.assertEquals("test@FS.com", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_false_pre_condition_should_find_the_pattern_but_not_replace_it() {
		ConditionalPatternReplacementFeatureAdjuster featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("fortscale", "FS");
		featureAdjuster.setPreReplacementCondition(".+@.+\\.com");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "test@fortscale.dom");
		featureExtractor.extract(message);
		Assert.assertEquals("test@fortscale.dom", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_true_post_condition_should_find_the_pattern_and_replace_it() {
		ConditionalPatternReplacementFeatureAdjuster featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("\\d", "");
		featureAdjuster.setPostReplacementCondition("(.*[a-zA-Z]){5,}.*");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "FS01_comp42");
		featureExtractor.extract(message);
		Assert.assertEquals("FS_comp", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_false_post_condition_should_find_the_pattern_but_not_replace_it() {
		ConditionalPatternReplacementFeatureAdjuster featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("\\d", "");
		featureAdjuster.setPostReplacementCondition("(.*[a-zA-Z]){5,}.*");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "FS02_a1");
		featureExtractor.extract(message);
		Assert.assertEquals("FS02_a1", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_true_pre_condition_and_true_post_condition_should_find_and_replace_the_pattern() {
		ConditionalPatternReplacementFeatureAdjuster featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("\\d", "");
		featureAdjuster.setPreReplacementCondition("FS_.+");
		featureAdjuster.setPostReplacementCondition("FS_.*[a-zA-Z].*");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "FS_12RD34");
		featureExtractor.extract(message);
		Assert.assertEquals("FS_RD", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_true_pre_condition_should_not_find_the_pattern_and_return_original_value() {
		ConditionalPatternReplacementFeatureAdjuster featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("fortscale", "FS");
		featureAdjuster.setPreReplacementCondition(".+@.+\\.com");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "user@unicorn.com");
		featureExtractor.extract(message);
		Assert.assertEquals("user@unicorn.com", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}
}
