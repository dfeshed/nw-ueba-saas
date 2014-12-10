package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import org.mockito.Mockito;

/**
 * Created by rotemn on 10/26/2014.
 */
public class DataQueryGeneratorTest {

	public static String dto1 = "{\"fields\":[],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"sort\":[{\"field\":{\"id\":\"event_score\"},\"direction\":\"DESC\"},{\"field\":{\"id\":\"event_time\"},\"direction\":\"DESC\"}],\"limit\":20,\"offset\":0}";
	public static String joinDTOJson = "{\"fields\":[{\"entity\":\"kerberos_logins\",\"allFields\":true},{\"entity\":\"users\",\"id\":\"displayName\"},{\"entity\":\"users\",\"id\":\"id\"},{\"entity\":\"users\",\"id\":\"is_user_administrator\"},{\"entity\":\"users\",\"id\":\"is_user_executive\"},{\"entity\":\"users\",\"id\":\"accountIsDisabled\"},{\"entity\":\"users\",\"id\":\"is_user_service\"},{\"entity\":\"users\",\"id\":\"followed\"}],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418121428,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1418207828,\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\",\"users\"],\"join\":[{\"type\":\"RIGHT\",\"entity\":\"users\",\"joinFields\":[{\"entity\":\"kerberos_logins\",\"field\":\"normalized_username\"},{\"entity\":\"users\",\"field\":\"username\"}]}],\"sort\":[],\"limit\":50,\"offset\":0}";

	protected DataEntitiesConfig dataEntitiesConfig;
	protected DataQueryDTO dataQueryDTO1;
	protected DataQueryDTO joinDTO;

	protected ObjectMapper mapper = new ObjectMapper();
	public void setUp()
					throws Exception {

		dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTO.class);
		joinDTO = mapper.readValue(joinDTOJson, DataQueryDTO.class);
		dataEntitiesConfig = Mockito.mock(DataEntitiesConfig.class);

		/*
		Mockito.when(dataEntitiesConfig.getEntityPerformanceTable(Mockito.any(String.class))).thenReturn("somePerformanceEntityField");
		Mockito.when(dataEntitiesConfig.getEntityTable(Mockito.any(String.class))).thenReturn("someEntity");
		Mockito.when(dataEntitiesConfig.getEntityPerformanceTableField(Mockito.any(String.class))).thenReturn("somePerformanceEntityField");
		Mockito.when(dataEntitiesConfig.getEntityPerformanceTableFieldMinValue(Mockito.any(String.class))).thenReturn(50);*/

	}






}
