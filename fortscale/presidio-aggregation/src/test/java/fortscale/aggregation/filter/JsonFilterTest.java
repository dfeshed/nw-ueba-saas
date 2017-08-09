package fortscale.aggregation.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.InvalidPathException;

/**
 * Created by orend on 14/07/2015.
 */

public class JsonFilterTest {
	private static final String JSON_OBJECT_STR = "{ \"store\": { \"book\": [ { \"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95 }, { \"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99 }, { \"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99 }, { \"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99 } ], \"bicycle\": { \"color\": \"red\", \"price\": 19.95 } }, \"expensive\": 10 }";
	private static final JSONObject JSON_OBJECT = getJsonObject(JSON_OBJECT_STR);
	
	private static JSONObject getJsonObject(String jsonObjectStr){
		JSONObject ret = null;
		try {
			ret = (new ObjectMapper()).readValue(jsonObjectStr.getBytes("UTF-8"), JSONObject.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return ret;
	}

	@Test
	public void testPassedFilter(){
		String jsonPath = "$.store.book[*].author";

		JsonFilter filter = new JsonFilter(jsonPath);
		Assert.assertTrue(filter.passedFilter(JSON_OBJECT));
	}

	@Test(expected = InvalidPathException.class)
	public void testInvalidPathPassedFilter() throws Exception {
		String jsonPath = "Invalid Json Path";

		JsonFilter filter = new JsonFilter(jsonPath);
		filter.passedFilter(JSON_OBJECT);
	}

	@Test
	public void testDoesntPassedComplexFilter(){
		String jsonPath = "$.featureExtractorList[*].featureAdjustor.featureAdjustorList[?(@.additionToDenominator < 3 && @.type == 'number_divider_feature_adjustor')]";
		String EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST = "{\"featureExtractorType\":\"priority_container_feature_extractor\",\"featureExtractorList\":[{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org1\",\"featureAdjustor\":{\"type\":\"chain_feature_adjustor\",\"featureAdjustorList\":[{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"_\",\"replacement\":\".\"},{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"prefix\",\"replacement\":\"\"},{\"type\":\"number_divider_feature_adjustor\",\"additionToDenominator\":4.5,\"denominatorFieldName\":\"duration\"},{\"type\":\"inv_val_feature_adjustor\",\"denominator\":0.1}]}},{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org2\",\"featureAdjustor\":{\"type\":\"ipv4_feature_adjustor\",\"subnetMask\":20}}]}";
		JSONObject objectValue = getJsonObject(EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST);

		JsonFilter filter = new JsonFilter(jsonPath);
		Assert.assertFalse(filter.passedFilter(objectValue));
	}
	
	@Test
	public void testPassedComplexFilter(){
		String jsonPath = "$.featureExtractorList[*].featureAdjustor.featureAdjustorList[?(@.additionToDenominator > 3 && @.type == 'number_divider_feature_adjustor')]";
		String EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST = "{\"featureExtractorType\":\"priority_container_feature_extractor\",\"featureExtractorList\":[{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org1\",\"featureAdjustor\":{\"type\":\"chain_feature_adjustor\",\"featureAdjustorList\":[{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"_\",\"replacement\":\".\"},{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"prefix\",\"replacement\":\"\"},{\"type\":\"number_divider_feature_adjustor\",\"additionToDenominator\":4.5,\"denominatorFieldName\":\"duration\"},{\"type\":\"inv_val_feature_adjustor\",\"denominator\":0.1}]}},{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org2\",\"featureAdjustor\":{\"type\":\"ipv4_feature_adjustor\",\"subnetMask\":20}}]}";
		JSONObject objectValue = getJsonObject(EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST);

		JsonFilter filter = new JsonFilter(jsonPath);
		Assert.assertTrue(filter.passedFilter(objectValue));
	}

	@Test
	public void testEmptyPathPassedFilter(){
		String EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST = "{\"featureExtractorType\":\"priority_container_feature_extractor\",\"featureExtractorList\":[{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org1\",\"featureAdjustor\":{\"type\":\"chain_feature_adjustor\",\"featureAdjustorList\":[{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"_\",\"replacement\":\".\"},{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"prefix\",\"replacement\":\"\"},{\"type\":\"number_divider_feature_adjustor\",\"additionToDenominator\":4.5,\"denominatorFieldName\":\"duration\"},{\"type\":\"inv_val_feature_adjustor\",\"denominator\":0.1}]}},{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org2\",\"featureAdjustor\":{\"type\":\"ipv4_feature_adjustor\",\"subnetMask\":20}}]}";
		JSONObject objectValue = getJsonObject(EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST);

		JsonFilter filter = new JsonFilter("");
		Assert.assertTrue(filter.passedFilter(objectValue));
	}
	
	@Test
	public void testNoListElementPassedFilter(){
		String jsonPath = "[?(@.EventScore < 3)]";
		String noListEventToTestStr = "{\"EventScore\":2.0, \"timescore\": 50.0}";
		JSONObject objectValue = getJsonObject(noListEventToTestStr);
		
		JsonFilter filter = new JsonFilter(jsonPath);
		Assert.assertTrue(filter.passedFilter(objectValue));
	}
	
	@Test
	public void testNoListElementNotPassedFilter(){
		String jsonPath = "[?(@.EventScore > 2)]";
		String noListEventToTestStr = "{\"EventScore\":2.0, \"timescore\": 50.0}";
		JSONObject objectValue = getJsonObject(noListEventToTestStr);
		
		JsonFilter filter = new JsonFilter(jsonPath);
		Assert.assertFalse(filter.passedFilter(objectValue));
	}
	
	@Test
	public void testNoListElementPassedComplexFilter(){
		String jsonPath = "[?(@.event.scores.EventScore.score <= 2)]";
		String noListEventToTestStr = "{\"event\": {\"date_source\":\"ssh\", \"scores\":{\"EventScore\":{\"score\":2.0, \"timescore\": 50.0}, \"avg_score\": 30.0}, \"username\":\"name1\"}}";
		JSONObject objectValue = getJsonObject(noListEventToTestStr);
		
		JsonFilter filter = new JsonFilter(jsonPath);
		Assert.assertTrue(filter.passedFilter(objectValue));
	}
	
	@Test
	public void testNoListElementNotPassedComplexFilter(){
		String jsonPath = "[?(@.event.scores.EventScore.score > 2)]";
		String noListEventToTestStr = "{\"event\": {\"date_source\":\"ssh\", \"scores\":{\"EventScore\":{\"score\":2.0, \"timescore\": 50.0}, \"avg_score\": 30.0}, \"username\":\"name1\"}}";
		JSONObject objectValue = getJsonObject(noListEventToTestStr);
		
		JsonFilter filter = new JsonFilter(jsonPath);
		Assert.assertFalse(filter.passedFilter(objectValue));
	}
}
