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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

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
		when(dataEntitiesConfig.getEntityPartitionStrategy(dataQueryDTO1.getEntities()[0])).thenReturn(partitionStrategy);
		ArrayList<String> partitionsBaseFields = new ArrayList<String>();
		partitionsBaseFields.add("event_time_utc");

		when(dataEntitiesConfig.getEntityPartitionBaseField(any(String.class))).thenReturn(partitionsBaseFields);
		when(dataEntitiesConfig.getFieldColumn(any(String.class), any(String.class))).thenReturn("date_time_unix");
		when(dataEntitiesConfig.getFieldColumn(any(String.class), eq("yearmonthday"))).thenReturn("date_time_unix");

		when(dataEntitiesConfig.getFieldColumn(any(String.class), eq("event_score"))).thenReturn("eventscore");

		when(dataEntitiesConfig.getFieldType(any(String.class), eq("yearmonthday"),any(Boolean.class))).thenReturn(QueryValueType.TIMESTAMP);
		when(dataEntitiesConfig.getFieldType(any(String.class), eq("event_score"),any(Boolean.class))).thenReturn(QueryValueType.NUMBER);
		when(dataEntitiesConfig.getFieldType(any(String.class), eq("user_name"),any(Boolean.class))).thenReturn(QueryValueType.STRING);
		when(dataEntitiesConfig.getFieldType(any(String.class), eq("date_time_unix"),any(Boolean.class))).thenReturn(QueryValueType.TIMESTAMP);
		when(dataEntitiesConfig.getFieldType(any(String.class), eq("event_time_utc"),any(Boolean.class))).thenReturn(QueryValueType.TIMESTAMP);


	}

	@Test
	public void testGenerateQueryPart()
			throws Exception {

		String sqlStr = mySqlWherePartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "WHERE yearmonthday >= 20141024 AND yearmonthday <= 20141026 AND (eventscore >= 50 AND date_time_unix >= 1414184400 AND date_time_unix <= 1414360799)";
		assertEquals("SQL Where part for DTO1" , expectedString, sqlStr);
	}

	@Test
	public void mySqlWherePartGenerator_should_generate_correct_where_condition()
			throws Exception {
		String sqlStr = mySqlWherePartGenerator.generateQueryPart(complexWhereDTO);
		String expectedString = "WHERE yearmonthday >= 20141024 AND yearmonthday <= 20141026 AND (date_time_unix >= 1414184400 AND date_time_unix <= 1414360799 AND eventscore IN ( 50 , 70 ) AND date_time_unix IN ( \"my_user_name\" ) AND date_time_unix BETWEEN  \"my_user_name1\" AND \"my_user_name2\"  AND date_time_unix BETWEEN  1414360799 AND 1414360800  AND date_time_unix LIKE \"%my_user_name\" AND date_time_unix LIKE \"my_user_name%\" AND date_time_unix LIKE \"%my_user_name%\" AND eventscore IS NOT NULL  AND eventscore IS NULL )";
		assertEquals("SQL Where part for complexWhereDTO" , expectedString, sqlStr);
	}
}

