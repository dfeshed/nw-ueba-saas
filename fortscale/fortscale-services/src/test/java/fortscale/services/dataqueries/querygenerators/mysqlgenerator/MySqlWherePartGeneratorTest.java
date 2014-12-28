package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.QueryValueType;
import fortscale.services.dataqueries.DataQueryGeneratorTest;
import fortscale.services.dataqueries.querydto.*;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MySqlWherePartGeneratorTest extends DataQueryGeneratorTest {

	private MySqlWherePartGenerator mySqlWherePartGenerator;
	private MySqlValueGenerator mySqlValueGenerator;

	@Before
	public void setUp()
			throws Exception {

		super.setUp();
		mySqlValueGenerator = new MySqlValueGenerator();
		mySqlWherePartGenerator = new MySqlWherePartGenerator();

		mySqlWherePartGenerator.setDataEntitiesConfig(dataEntitiesConfig);
        MySqlFieldGenerator fieldGenerator = new MySqlFieldGenerator();
        fieldGenerator.setDataQueryDtoHelper(dataQueryDtoHelper);
        fieldGenerator.setMySqlValueGenerator(mySqlValueGenerator);
        fieldGenerator.setDataEntitiesConfig(dataEntitiesConfig);
        mySqlWherePartGenerator.setMySqlFieldGenerator(fieldGenerator);
		mySqlWherePartGenerator.setMySqlValueGenerator(mySqlValueGenerator);
        mySqlWherePartGenerator.setDataQueryDtoHelper(dataQueryDtoHelper);

        /*
		for (Term childTerm: dataQueryDTO1.getConditions().getTerms()){
			if (childTerm instanceof ConditionField){
				ConditionField condition = (ConditionField)childTerm;
                Mockito.when(mySqlFieldGenerator.generateSql(Mockito.any(DataQueryField.class), Mockito.any(DataQueryDTO.class), Mockito.any(Boolean.class), Mockito.any(Boolean.class))).thenReturn(condition.getField().getId());
            }
		}
*/
		PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy("daily");
		Mockito.when(dataEntitiesConfig.getEntityPartitionStrategy(dataQueryDTO1.getEntities()[0])).thenReturn(partitionStrategy);
		ArrayList<String> partitionsBaseFields = new ArrayList<String>();
		partitionsBaseFields.add("event_time_utc");

		Mockito.when(dataEntitiesConfig.getEntityPartitionBaseField(dataQueryDTO1.getEntities()[0])).thenReturn(partitionsBaseFields);
		Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO1.getEntities()[0],partitionsBaseFields.get(0))).thenReturn("date_time_unix");
		Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO1.getEntities()[0],"yearmonthday")).thenReturn("date_time_unix");

		Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO1.getEntities()[0],"event_score")).thenReturn("eventscore");

		Mockito.when(dataEntitiesConfig.getFieldType(dataQueryDTO1.getEntities()[0], "yearmonthday")).thenReturn(QueryValueType.DATE_TIME);
		Mockito.when(dataEntitiesConfig.getFieldType(dataQueryDTO1.getEntities()[0], "event_score")).thenReturn(QueryValueType.NUMBER);
		Mockito.when(dataEntitiesConfig.getFieldType(dataQueryDTO1.getEntities()[0], "date_time_unix")).thenReturn(QueryValueType.DATE_TIME);
		Mockito.when(dataEntitiesConfig.getFieldType(dataQueryDTO1.getEntities()[0], "event_time_utc")).thenReturn(QueryValueType.DATE_TIME);


	}

	@Test
	public void testGenerateQueryPart()
			throws Exception {

		String sqlStr = mySqlWherePartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "WHERE yearmonthday >= 20141024 AND yearmonthday <= 20141026 AND (eventscore >= 50 AND date_time_unix >= 1414184400 AND date_time_unix <= 1414360799)";
		assertEquals("SQL Where part for DTO1" , expectedString, sqlStr);
	}
}

