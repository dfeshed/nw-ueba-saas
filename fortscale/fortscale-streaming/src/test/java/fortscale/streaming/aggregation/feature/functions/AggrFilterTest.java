package fortscale.streaming.aggregation.feature.functions;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import org.eclipse.jdt.internal.core.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by orend on 14/07/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/aggr-feature-service-context-test.xml" })
public class AggrFilterTest {

	private static final String JSON_OBJECT = "{ \"store\": { \"book\": [ { \"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95 }, { \"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99 }, { \"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99 }, { \"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99 } ], \"bicycle\": { \"color\": \"red\", \"price\": 19.95 } }, \"expensive\": 10 }";

	@Test
	public void testPassedFilter() throws Exception{
		String jsonPath = "$..store.book[*].author";
		Object objectValue = Configuration.defaultConfiguration().jsonProvider().parse(JSON_OBJECT);

		AggrFilter filter = new AggrFilter(jsonPath);
		Assert.isTrue(filter.passedFilter("name1", objectValue));
	}

	@Test(expected = InvalidPathException.class)
	public void testInvalidPathPassedFilter() throws Exception {
		String jsonPath = "Invalid Json Path";
		Object objectValue = Configuration.defaultConfiguration().jsonProvider().parse(JSON_OBJECT);

		AggrFilter filter = new AggrFilter(jsonPath);
		filter.passedFilter("name1", objectValue);
	}

	@Test
	public void testDoesntPassedFilter() throws Exception{
		String jsonPath = "$.featureExtractorList[*].featureAdjustor.featureAdjustorList[?(@.additionToDenominator < 3 && @.type == 'number_divider_feature_adjustor')]";
		String EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST = "{\"featureExtractorType\":\"priority_container_feature_extractor\",\"featureExtractorList\":[{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org1\",\"featureAdjustor\":{\"type\":\"chain_feature_adjustor\",\"featureAdjustorList\":[{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"_\",\"replacement\":\".\"},{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"prefix\",\"replacement\":\"\"},{\"type\":\"number_divider_feature_adjustor\",\"additionToDenominator\":4.5,\"denominatorFieldName\":\"duration\"},{\"type\":\"inv_val_feature_adjustor\",\"denominator\":0.1}]}},{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org2\",\"featureAdjustor\":{\"type\":\"ipv4_feature_adjustor\",\"subnetMask\":20}}]}";
		Object objectValue = Configuration.defaultConfiguration().jsonProvider().parse(EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST);

		AggrFilter filter = new AggrFilter(jsonPath);
		Assert.isTrue(!filter.passedFilter("name1", objectValue));
	}

	@Test
	public void testEmptyPathPassedFilter() throws Exception {
		String EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST = "{\"featureExtractorType\":\"priority_container_feature_extractor\",\"featureExtractorList\":[{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org1\",\"featureAdjustor\":{\"type\":\"chain_feature_adjustor\",\"featureAdjustorList\":[{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"_\",\"replacement\":\".\"},{\"type\":\"pattern_replacment_feature_adjustor\",\"pattern\":\"prefix\",\"replacement\":\"\"},{\"type\":\"number_divider_feature_adjustor\",\"additionToDenominator\":4.5,\"denominatorFieldName\":\"duration\"},{\"type\":\"inv_val_feature_adjustor\",\"denominator\":0.1}]}},{\"featureExtractorType\":\"event_feature_extractor\",\"fieldName\":\"org2\",\"featureAdjustor\":{\"type\":\"ipv4_feature_adjustor\",\"subnetMask\":20}}]}";
		Object objectValue = Configuration.defaultConfiguration().jsonProvider().parse(EVENT_FEATURE_EXTRACTOR_JSON_TO_TEST);

		AggrFilter filter = new AggrFilter("");
		Assert.isTrue(filter.passedFilter("name1", objectValue));
	}
}
