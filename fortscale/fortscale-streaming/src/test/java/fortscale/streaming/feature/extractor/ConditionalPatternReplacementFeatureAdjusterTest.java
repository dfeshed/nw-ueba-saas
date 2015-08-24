package fortscale.streaming.feature.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class ConditionalPatternReplacementFeatureAdjusterTest {
	private static final String FEATURE_ADJUSTER_FORMAT = "{\"type\":\"conditional_pattern_replacement_feature_adjuster\",\"pattern\":\"%s\",\"replacement\":\"%s\",\"preReplacementCondition\":%s,\"postReplacementCondition\":%s}";
	private static final String FEATURE_EXTRACTOR_FORMAT = "{\"type\":\"event_feature_extractor\",\"originalFieldName\":\"%s\",\"normalizedFieldName\":\"%s\",\"featureAdjustor\":%s}";
	private static final String DEFAULT_ORIGINAL_FIELD_NAME = "before";
	private static final String DEFAULT_NORMALIZED_FIELD_NAME = "after";

	@Test
	public void feature_extractor_should_be_deserialized_from_string() throws Exception {
		String featureAdjuster = String.format(FEATURE_ADJUSTER_FORMAT, "ABC", "", null, "\".*_.*\"");
		String featureExtractor = String.format(FEATURE_EXTRACTOR_FORMAT, DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);

		FeatureExtractor actualFeatureExtractor = (new ObjectMapper()).readValue(featureExtractor, FeatureExtractor.class);
		Assert.assertNotNull(actualFeatureExtractor);

		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "start_ABC_end");

		String expected = "start__end";
		Object actual = actualFeatureExtractor.extract(message);
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected, message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_should_find_the_pattern_and_replace_it() {
		FeatureAdjustor featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("\\d", "0", null, null);
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "a1b2c3d4e5");
		featureExtractor.extract(message);
		Assert.assertEquals("a0b0c0d0e0", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_should_not_find_the_pattern_and_should_not_make_any_changes() {
		FeatureAdjustor featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("[a-z]{2}$", "-", null, null);
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "endOfLINE");
		featureExtractor.extract(message);
		Assert.assertEquals("endOfLINE", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_true_pre_condition_should_find_the_pattern_and_replace_it() {
		FeatureAdjustor featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("fortscale", "FS", ".+@.+\\.com", null);
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "test@fortscale.com");
		featureExtractor.extract(message);
		Assert.assertEquals("test@FS.com", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_false_pre_condition_should_find_the_pattern_but_not_replace_it() {
		FeatureAdjustor featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("fortscale", "FS", ".+@.+\\.com", null);
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "test@fortscale.dom");
		featureExtractor.extract(message);
		Assert.assertEquals("test@fortscale.dom", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_true_post_condition_should_find_the_pattern_and_replace_it() {
		FeatureAdjustor featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("\\d", "", null, "(.*[a-zA-Z]){5,}.*");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "FS01_comp42");
		featureExtractor.extract(message);
		Assert.assertEquals("FS_comp", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_false_post_condition_should_find_the_pattern_but_not_replace_it() {
		FeatureAdjustor featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("\\d", "", null, "(.*[a-zA-Z]){5,}.*");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "FS02_a1");
		featureExtractor.extract(message);
		Assert.assertEquals("FS02_a1", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_true_pre_condition_and_true_post_condition_should_find_and_replace_the_pattern() {
		FeatureAdjustor featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("\\d", "", "FS_.+", "FS_.*[a-zA-Z].*");
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "FS_12RD34");
		featureExtractor.extract(message);
		Assert.assertEquals("FS_RD", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}

	@Test
	public void feature_extractor_with_true_pre_condition_should_not_find_the_pattern_and_return_original_value() {
		FeatureAdjustor featureAdjuster = new ConditionalPatternReplacementFeatureAdjuster("fortscale", "FS", ".+@.+\\.com", null);
		FeatureExtractor featureExtractor = new EventFeatureExtractor(DEFAULT_ORIGINAL_FIELD_NAME, DEFAULT_NORMALIZED_FIELD_NAME, featureAdjuster);
		JSONObject message = new JSONObject();
		message.put(DEFAULT_ORIGINAL_FIELD_NAME, "user@unicorn.com");
		featureExtractor.extract(message);
		Assert.assertEquals("user@unicorn.com", message.get(DEFAULT_NORMALIZED_FIELD_NAME));
	}
}
