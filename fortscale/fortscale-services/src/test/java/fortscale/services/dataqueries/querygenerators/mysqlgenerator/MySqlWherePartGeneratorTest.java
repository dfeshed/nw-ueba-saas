package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.QueryValueType;
import fortscale.services.dataqueries.DataQueryGeneratorTest;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import org.junit.Before;
import org.junit.Test;

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


		PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy("daily");
		when(dataEntitiesConfig.getEntityPartitionStrategy(dataQueryDTO1.getEntities()[0])).thenReturn(partitionStrategy);
		ArrayList<String> partitionsBaseFields = new ArrayList<String>();
		partitionsBaseFields.add("event_time_utc");

		when(dataEntitiesConfig.getEntityPartitionBaseField(any(String.class))).thenReturn(partitionsBaseFields);
		when(dataEntitiesConfig.getEntityTable(any(String.class))).thenReturn("entity");
		when(dataEntitiesConfig.getFieldTable(any(String.class),any(String.class))).thenReturn("entity.");
		when(dataEntitiesConfig.getFieldColumn(any(String.class), any(String.class))).thenReturn("date_time_unix");
		when(dataEntitiesConfig.getFieldColumn(any(String.class), eq("yearmonthday"))).thenReturn("date_time_unix");

		when(dataEntitiesConfig.getFieldColumn(any(String.class), eq("event_score"))).thenReturn("eventscore");
		when(dataEntitiesConfig.getFieldColumn(any(String.class), eq("user_name"))).thenReturn("user_name");

		when(dataEntitiesConfig.getFieldType(any(String.class), eq("yearmonthday"),any(Boolean.class))).thenReturn(QueryValueType.TIMESTAMP);
		when(dataEntitiesConfig.getFieldType(any(String.class), eq("event_score"),any(Boolean.class))).thenReturn(QueryValueType.NUMBER);
		when(dataEntitiesConfig.getFieldType(any(String.class), eq("user_name"),any(Boolean.class))).thenReturn(QueryValueType.STRING);
		when(dataEntitiesConfig.getFieldType(any(String.class), eq("date_time_unix"),any(Boolean.class))).thenReturn(QueryValueType.TIMESTAMP);
		when(dataEntitiesConfig.getFieldType(any(String.class), eq("event_time_utc"),any(Boolean.class))).thenReturn(QueryValueType.TIMESTAMP);
		when(dataEntitiesConfig.getFieldColumn(any(String.class), eq("session_time_utc"))).thenReturn("start_time BETWEEN {0} AND {1} OR end_time BETWEEN {0} AND {1}");
		when(dataEntitiesConfig.getFieldIsTokenized(eq("vpn_session"), eq("session_time_utc"))).thenReturn(true);


	}

	@Test
	public void testGenerateQueryPart()
			throws Exception {

		String sqlStr = mySqlWherePartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "WHERE (entity.yearmonthday >= 20141024) AND (entity.yearmonthday <= 20141026) AND ((entity.eventscore >= 50) AND (entity.date_time_unix >= 1414184400) AND (entity.date_time_unix <= 1414360799))";
		assertEquals("SQL Where part for DTO1" , expectedString, sqlStr);
	}

	@Test
	public void mySqlWherePartGenerator_should_generate_correct_where_condition()
			throws Exception {
		String sqlStr = mySqlWherePartGenerator.generateQueryPart(complexWhereDTO);
		String expectedString = "WHERE (entity.yearmonthday >= 20141024) AND (entity.yearmonthday <= 20141026) AND ((entity.date_time_unix >= 1414184400) AND (entity.date_time_unix <= 1414360799) AND (entity.eventscore IN ( 50 , 70 )) AND (lower(entity.user_name) IN ( \"my_user_name\" )) AND (lower(entity.user_name) BETWEEN  \"my_user_name1\" AND \"my_user_name2\" ) AND (entity.date_time_unix BETWEEN  1414360799 AND 1414360800 ) AND (lower(entity.user_name) LIKE \"%my_user_name\") AND (lower(entity.user_name) LIKE \"my_user_name%\") AND (lower(entity.user_name) LIKE \"%my_user_name%\") AND (entity.eventscore IS NOT NULL ) AND (entity.eventscore IS NULL ) AND (lower(entity.user_name) IS NULL  OR lower(entity.user_name) ='' ) AND (lower(entity.user_name) IS NOT NULL  AND lower(entity.user_name) != '' ))";
		assertEquals("SQL Where part for complexWhereDTO" , expectedString, sqlStr);
	}

	@Test
	public void mySqlWherePartGenerator_tokenize_values_in_expression()
			throws Exception {
		String sqlStr = mySqlWherePartGenerator.generateQueryPart(tokenizedExpression);
		String expectedString = "WHERE ((entity.start_time BETWEEN 1427407200 AND 1430168399 OR end_time BETWEEN 1427407200 AND 1430168399 ) AND (entity.date_time_unix >= 50))";
		assertEquals("SQL Where part for complexWhereDTO" , expectedString, sqlStr);
	}

    @Test
    public void mySqlWherePartGenerator_between_date_time()
            throws Exception {
        String sqlStr = mySqlWherePartGenerator.generateQueryPart(betweenPartitionDTO);
        String expectedString = "WHERE (entity.yearmonthday >= 20141024) AND (entity.yearmonthday <= 20141026) AND ((entity.date_time_unix BETWEEN  1414184400 AND 1414360799 ))";
        assertEquals("SQL Where part for betweenPartitionDTO" , expectedString, sqlStr);
    }
}

