package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querydto.QuerySort;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MySqlGroupByPartGeneratorTest extends DataQueryGeneratorTest{

	private MySqlGroupByPartGenerator mySqlGroupByPartGenerator;
	private MySqlFieldGenerator mySqlFieldGenerator;

	private DataQueryDTO dataQueryDTO2;

	@Before
	public void setUp()
					throws Exception {

		super.setUp();
		mySqlGroupByPartGenerator = new MySqlGroupByPartGenerator();
		mySqlFieldGenerator = Mockito.mock(MySqlFieldGenerator.class);
		mySqlGroupByPartGenerator.setMySqlFieldGenerator(mySqlFieldGenerator);

		dataQueryDTO2 = mapper.readValue(dto1, DataQueryDTO.class);
		ArrayList<DataQueryField> groupBy = new ArrayList<>();
		DataQueryField field = new DataQueryField();
		field.setId("aaa");
		groupBy.add(field);
		field = new DataQueryField();
		field.setId("bbb");
		groupBy.add(field);
		dataQueryDTO2.setGroupBy(groupBy);

		for (DataQueryField dataQueryField : dataQueryDTO2.getGroupBy()) {
			Mockito.when(mySqlFieldGenerator.generateSql(dataQueryField, dataQueryDTO2)).thenReturn(dataQueryField.getId());
		}
	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {

		// empty group-by
		String sqlStr = mySqlGroupByPartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "";
		assertEquals("SQL GroupBy Part for DTO1" , expectedString, sqlStr);

		// 2 fields group by
		sqlStr = mySqlGroupByPartGenerator.generateQueryPart(dataQueryDTO2);
		expectedString = "GROUP BY aaa, bbb";
		assertEquals("SQL GroupBy Part for DTO2" , expectedString, sqlStr);

	}
}
