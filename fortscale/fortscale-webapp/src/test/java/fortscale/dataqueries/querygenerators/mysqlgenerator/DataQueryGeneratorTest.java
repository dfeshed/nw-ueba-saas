package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.dataqueries.DataEntitiesConfig;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.services.exceptions.InvalidValueException;
import org.junit.Before;
import org.mockito.Mockito;

/**
 * Created by rotemn on 10/26/2014.
 */
public class DataQueryGeneratorTest {

	public static String dto1 = "{\"fields\":[],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"sort\":[{\"field\":{\"id\":\"event_score\"},\"direction\":\"DESC\"},{\"field\":{\"id\":\"event_time\"},\"direction\":\"DESC\"}],\"limit\":20,\"offset\":0}";

	protected MySqlUtils mySqlUtils;
	protected DataEntitiesConfig dataEntitiesConfig;
	protected DataQueryDTO dataQueryDTO1;

	private ObjectMapper mapper = new ObjectMapper();
	public void setUp()
					throws Exception {

		dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTO.class);

		dataEntitiesConfig = Mockito.mock(DataEntitiesConfig.class);

		Mockito.when(dataEntitiesConfig.getEntityPerformanceTable(Mockito.any(String.class))).thenReturn("somePerformanceEntityField");
		Mockito.when(dataEntitiesConfig.getEntityTable(Mockito.any(String.class))).thenReturn("someEntity");
		Mockito.when(dataEntitiesConfig.getEntityPerformanceTableField(Mockito.any(String.class))).thenReturn("somePerformanceEntityField");
		Mockito.when(dataEntitiesConfig.getEntityPerformanceTableFieldMinValue(Mockito.any(String.class))).thenReturn(50);

	}






}
