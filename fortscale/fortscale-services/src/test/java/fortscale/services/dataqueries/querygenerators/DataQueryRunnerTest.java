package fortscale.services.dataqueries.querygenerators;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;
import fortscale.services.exceptions.InvalidValueException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/fortscale-services-context-dataEntity-test.xml"})
public class DataQueryRunnerTest implements EmbeddedValueResolverAware {

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.stringValueResolver = resolver;
	}

	//resolver for properties resolving values
	protected StringValueResolver stringValueResolver;


	DataEntitiesConfig dataEntitiesConfig = new DataEntitiesConfig();

	//the json that will represent base entity request as main entity
	public static String dto1 = "{\n" +
			"    \"fields\": [\n" +
			"        {\n" +
			"            \"allFields\": true\n" +
			"        },\n" +
			"        {\n" +
			"            \"entity\": \"users\",\n" +
			"            \"id\": \"display_name\"\n" +
			"        },\n" +
			"        {\n" +
			"            \"entity\": \"users\",\n" +
			"            \"id\": \"followed\"\n" +
			"        },\n" +
			"        {\n" +
			"            \"entity\": \"users\",\n" +
			"            \"id\": \"account_is_disabled\"\n" +
			"        },\n" +
			"        {\n" +
			"            \"entity\": \"users\",\n" +
			"            \"id\": \"is_user_service\"\n" +
			"        }\n" +
			"    ],\n" +
			"    \"entities\": [\n" +
			"        \"scored_access_event\"\n" +
			"    ],\n" +
			"    \"sort\": [\n" +
			"        {\n" +
			"            \"field\": {\n" +
			"                \"id\": \"event_score\"\n" +
			"            },\n" +
			"            \"direction\": \"DESC\"\n" +
			"        },\n" +
			"        {\n" +
			"            \"field\": {\n" +
			"                \"id\": \"event_time\"\n" +
			"            },\n" +
			"            \"direction\": \"DESC\"\n" +
			"        }\n" +
			"    ],\n" +
			"    \"conditions\": {\n" +
			"        \"type\": \"term\",\n" +
			"        \"logicalOperator\": \"AND\",\n" +
			"        \"terms\": [\n" +
			"            {\n" +
			"                \"field\": {\n" +
			"                    \"id\": \"event_score\"\n" +
			"                },\n" +
			"                \"queryOperator\": \"greaterThanOrEquals\",\n" +
			"                \"type\": \"field\",\n" +
			"                \"value\": 50\n" +
			"            },\n" +
			"            {\n" +
			"                \"field\": {\n" +
			"                    \"id\": \"event_time_utc\"\n" +
			"                },\n" +
			"                \"queryOperator\": \"between\",\n" +
			"                \"type\": \"field\",\n" +
			"                \"value\": \"1425294318,1425380718\"\n" +
			"            }\n" +
			"        ]\n" +
			"    },\n" +
			"    \"limit\": 20,\n" +
			"    \"offset\": 0,\n" +
			"    \"join\": [\n" +
			"        {\n" +
			"            \"type\": \"RIGHT\",\n" +
			"            \"entity\": \"users\",\n" +
			"            \"left\": {\n" +
			"                \"entity\": \"scored_access_event\",\n" +
			"                \"field\": \"normalized_username\"\n" +
			"            },\n" +
			"            \"right\": {\n" +
			"                \"entity\": \"users\",\n" +
			"                \"field\": \"normalized_username\"\n" +
			"            }\n" +
			"        }\n" +
			"    ]\n" +
			"}";

	protected DataQueryDTO dataQueryDTO1;
	protected ObjectMapper mapper = new ObjectMapper();



	@Before
	public void setUp() throws Exception {

		dataEntitiesConfig.setEmbeddedValueResolver(stringValueResolver);
		//warm up the entities cache
		dataEntitiesConfig.getAllLogicalEntities();
		dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTO.class);



	}

	//todo - need to add to the test the validation on the join list (that also the join list was change due to the translation )
	@Test
	public void testTranslateAbstarctDataQuery_BaseCase() throws Exception {

		DataQueryRunner dataQueryRunner;
		try {
			dataQueryRunner = new MySqlQueryRunner();
		}
		catch(Exception error){
			throw new InvalidValueException("Couldn't create query generator: " + error.getMessage());
		}

		List<DataQueryDTO> excpectedResult = new ArrayList<>();
		DataQueryDTO dto1 = new DataQueryDTO(dataQueryDTO1);
		String[] entites1 = new String[1];
		entites1[0] = "kerberos_logins";
		dto1.setEntities(entites1);
		excpectedResult.add(dto1);

		DataQueryDTO dto2 = new DataQueryDTO(dataQueryDTO1);
		String[] entites2 = new String[1];
		entites2[0] = "ssh";
		dto2.setEntities(entites2);
		excpectedResult.add(dto2);


		DataQueryDTO dto3 = new DataQueryDTO(dataQueryDTO1);
		String[] entites3 = new String[1];
		entites3[0] = "vpn";
		dto3.setEntities(entites3);
		excpectedResult.add(dto3);

		List<DataQueryDTO> actualResult = dataQueryRunner.translateAbstarctDataQuery(dataQueryDTO1,dataEntitiesConfig);




		int assertCounter = 0;

		for (DataQueryDTO actualDataQueryDTO : actualResult)
		{


			for (DataQueryDTO expectedDataQueryDTO : excpectedResult)
			{
				if (expectedDataQueryDTO.getEntities()[0].equals(actualDataQueryDTO.getEntities()[0])) {
					assertCounter++;
					break;
				}
			}
		}

		assertTrue(actualResult.size() == excpectedResult.size() && assertCounter==excpectedResult.size() );




	}

}
