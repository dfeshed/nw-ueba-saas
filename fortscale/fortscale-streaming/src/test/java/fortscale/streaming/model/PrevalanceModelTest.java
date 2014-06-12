package fortscale.streaming.model;

import org.junit.Test;
import org.junit.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fortscale.streaming.model.field.DiscreetValuesModel;

public class PrevalanceModelTest {

	@Test
	public void model_should_serialize_to_json() throws Exception {
		// build model		
		PrevalanceModel model = new PrevalanceModel("my-model");
		FieldModel fieldModel = new DiscreetValuesModel();
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
		
        byte[] json = "{\"modelName\":\"my-model\",\"fields\":{\"fieldA\":{\"@class\":\"fortscale.streaming.model.field.DiscreetValuesModel\",\"counters\":{\"counts\":{\"500\":1}}}},\"timeMark\":0}".getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		PrevalanceModel model = mapper.readValue(json, PrevalanceModel.class);
		
		Assert.assertNotNull(model);
		Assert.assertEquals("my-model", model.getModelName());
	}
	
}
