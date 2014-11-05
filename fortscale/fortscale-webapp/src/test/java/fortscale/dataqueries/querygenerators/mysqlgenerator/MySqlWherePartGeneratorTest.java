package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.DataEntitiesConfig;
import fortscale.dataqueries.QueryValueType;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MySqlWherePartGeneratorTest extends DataQueryGeneratorTest{

	private MySqlWherePartGenerator mySqlWherePartGenerator;
	private MySqlFieldGenerator mySqlFieldGenerator;
	private MySqlValueGenerator mySqlValueGenerator;
	DataEntitiesConfig dataEntitiesConfig;


	@Before
	public void setUp()
			throws Exception {

		super.setUp();
		mySqlValueGenerator = new MySqlValueGenerator();
		mySqlWherePartGenerator = new MySqlWherePartGenerator();
		dataEntitiesConfig = Mockito.mock(DataEntitiesConfig.class);

		mySqlWherePartGenerator.setDataEntitiesConfig(dataEntitiesConfig);
		mySqlFieldGenerator = new MySqlFieldGenerator();
		mySqlFieldGenerator.setMySqlValueGenerator(mySqlValueGenerator);
		mySqlFieldGenerator.setDataEntitiesConfig(dataEntitiesConfig);
		mySqlWherePartGenerator.setMySqlFieldGenerator(mySqlFieldGenerator);
		mySqlWherePartGenerator.setMySqlValueGenerator(mySqlValueGenerator);

		/*for (DataQueryDTO.Term childTerm: dataQueryDTO1.conditions.terms){
			if (childTerm.getClass() == DataQueryDTO.ConditionField.class){
				DataQueryDTO.ConditionField condition = (DataQueryDTO.ConditionField)childTerm;
				Mockito.when(mySqlUtils.getConditionFieldSql(condition,dataQueryDTO1)).thenReturn(condition.field.getId() + "<=" + condition.getValue());
			}
		}*/

		PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy("daily");
		Mockito.when(dataEntitiesConfig.getEntityPartitionStrategy(dataQueryDTO1.entities[0])).thenReturn(partitionStrategy);
		ArrayList<String> partitionsBaseFields = new ArrayList<String>();
		partitionsBaseFields.add("event_time_utc");

		Mockito.when(dataEntitiesConfig.getEntityPartitionBaseField(dataQueryDTO1.entities[0])).thenReturn(partitionsBaseFields);
		Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO1.entities[0],partitionsBaseFields.get(0))).thenReturn("date_time_unix");
		Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO1.entities[0],"yearmonthday")).thenReturn("date_time_unix");

		Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO1.entities[0],"event_score")).thenReturn("eventscore");

		Mockito.when(dataEntitiesConfig.getFieldType(dataQueryDTO1.entities[0], "yearmonthday")).thenReturn(QueryValueType.DATE_TIME);
		Mockito.when(dataEntitiesConfig.getFieldType(dataQueryDTO1.entities[0], "event_score")).thenReturn(QueryValueType.NUMBER);
		Mockito.when(dataEntitiesConfig.getFieldType(dataQueryDTO1.entities[0], "date_time_unix")).thenReturn(QueryValueType.DATE_TIME);
		Mockito.when(dataEntitiesConfig.getFieldType(dataQueryDTO1.entities[0], "event_time_utc")).thenReturn(QueryValueType.DATE_TIME);



	}

	@Test
	public void testGenerateQueryPart()
			throws Exception {

		String sqlStr = mySqlWherePartGenerator.generateQueryPart(dataQueryDTO1);
		//TODO - check why this test is fail, it supposed to pass I believe it is something related to mockito
		String expectedString = "WHERE date_time_unix >= 20141024 AND date_time_unix <= 20141026 AND (eventscore >= 50 AND date_time_unix >= 1414184400 AND date_time_unix <= 1414360799)";
		assertEquals("SQL Where part for DTO1" , expectedString, sqlStr);

	}
}

