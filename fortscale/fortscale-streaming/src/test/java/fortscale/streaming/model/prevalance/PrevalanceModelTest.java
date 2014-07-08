package fortscale.streaming.model.prevalance;

import fortscale.streaming.model.FieldModel;
import fortscale.streaming.model.PrevalanceModel;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fortscale.streaming.model.field.DiscreetValuesCalibratedModel;

public class PrevalanceModelTest {

	@Test
	public void model_should_serialize_to_json() throws Exception {
		// build model		
		PrevalanceModel model = new PrevalanceModel("my-model");
		FieldModel fieldModel = new DiscreetValuesCalibratedModel();
		fieldModel.add(500, 100);
		fieldModel.add(500, 200);
		model.setFieldModel("fieldA", fieldModel);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		String json = mapper.writeValueAsString(model);

		//{"modelName":"my-model","fields":{"fieldA":{"@class":"fortscale.streaming.model.field.DiscreetValuesModel","counters":{"counts":{"500":1}}}},"timeMark":0}
		Assert.assertNotNull(json);
		Assert.assertTrue(json.contains("\"timeMark\":"));
		Assert.assertTrue(json.contains("\"modelName\":\"my-model\""));
		Assert.assertTrue(json.contains("\"fields\":"));
		Assert.assertTrue(json.contains("\"fieldA\":"));
	}
	
	@Test
	public void model_should_deserialize_from_json() throws Exception {
		
        byte[] json = "{\"modelName\":\"my-model\",\"fields\":{\"fieldA\":{\"@class\":\"fortscale.streaming.model.field.DiscreetValuesCalibratedModel\",\"featureCalibration\":{\"scoreBucketsAggr\":[2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615,2.5121061286922615],\"bucketScorerList\":[{\"featureValueToScoreMap\":{\"500\":2.5121061286922615},\"isFirstBucket\":true,\"score\":2.5121061286922615,\"scoreFeatureValue\":500},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null},{\"featureValueToScoreMap\":{},\"isFirstBucket\":false,\"score\":0.0,\"scoreFeatureValue\":null}],\"featureValueToCountMap\":{\"500\":2.0},\"addedValue\":1.0,\"total\":2.5121061286922615,\"minCount\":2.0,\"featureValueWithMinCount\":500}}},\"timeMark\":0}".getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		PrevalanceModel model = mapper.readValue(json, PrevalanceModel.class);
		
		Assert.assertNotNull(model);
		Assert.assertEquals("my-model", model.getModelName());
	}
	
}
