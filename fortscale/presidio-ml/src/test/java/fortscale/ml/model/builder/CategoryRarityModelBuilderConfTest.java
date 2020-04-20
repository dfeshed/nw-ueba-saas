package fortscale.ml.model.builder;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CategoryRarityModelBuilderConfTest {
	@Test
	public void should_deserialize_from_json_without_entries_to_save_in_model_field() throws IOException {
		JSONObject jsonObject = getCategoryRarityModelBuilderConfJsonObject(10, null);
		IModelBuilderConf conf = getModelBuilderConf(jsonObject);
		Assert.assertNotNull(conf);
		Assert.assertEquals(CategoryRarityModelBuilderConf.class, conf.getClass());
		Assert.assertEquals(10, ((CategoryRarityModelBuilderConf)conf).getNumOfBuckets());
		Assert.assertEquals(
				CategoryRarityModelBuilderConf.DEFAULT_ENTRIES_TO_SAVE_IN_MODEL,
				((CategoryRarityModelBuilderConf)conf).getEntriesToSaveInModel());
	}

	@Test
	public void should_deserialize_from_json_with_entries_to_save_in_model_field() throws IOException {
		int entriesToSaveInModel = CategoryRarityModelBuilderConf.DEFAULT_ENTRIES_TO_SAVE_IN_MODEL * 2;
		JSONObject jsonObject = getCategoryRarityModelBuilderConfJsonObject(20, entriesToSaveInModel);
		IModelBuilderConf conf = getModelBuilderConf(jsonObject);
		Assert.assertNotNull(conf);
		Assert.assertEquals(CategoryRarityModelBuilderConf.class, conf.getClass());
		Assert.assertEquals(20, ((CategoryRarityModelBuilderConf)conf).getNumOfBuckets());
		Assert.assertEquals(entriesToSaveInModel, ((CategoryRarityModelBuilderConf)conf).getEntriesToSaveInModel());
	}

	@Test(expected = JsonMappingException.class)
	public void should_fail_when_json_does_not_have_a_num_of_buckets_field() throws IOException {
		JSONObject jsonObject = getCategoryRarityModelBuilderConfJsonObject(null, null);
		getModelBuilderConf(jsonObject);
	}

	@Test(expected = JsonMappingException.class)
	public void should_fail_when_json_has_an_invalid_num_of_buckets_field() throws IOException {
		JSONObject jsonObject = getCategoryRarityModelBuilderConfJsonObject(-1, 30);
		getModelBuilderConf(jsonObject);
	}

	@Test(expected = JsonMappingException.class)
	public void should_fail_when_json_has_an_invalid_entries_to_save_in_model_field() throws IOException {
		JSONObject jsonObject = getCategoryRarityModelBuilderConfJsonObject(40, -10);
		getModelBuilderConf(jsonObject);
	}

	private static JSONObject getCategoryRarityModelBuilderConfJsonObject(
			Integer numOfBuckets, Integer entriesToSaveInModel) {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", CategoryRarityModelBuilderConf.CATEGORY_RARITY_MODEL_BUILDER);
		if (numOfBuckets != null) jsonObject.put("numOfBuckets", numOfBuckets);
		if (entriesToSaveInModel != null) jsonObject.put("entriesToSaveInModel", entriesToSaveInModel);
		return jsonObject;
	}

	private static IModelBuilderConf getModelBuilderConf(JSONObject jsonObject) throws IOException {
		return new ObjectMapper().readValue(jsonObject.toJSONString(), IModelBuilderConf.class);
	}
}
