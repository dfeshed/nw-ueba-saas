package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.querydto.QuerySort;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class MySqlOrderByPartGeneratorTest extends DataQueryGeneratorTest{

	private  MySqlOrderByPartGenerator mySqlOrderByPartGenerator;
	private MySqlFieldGenerator mySqlFieldGenerator;

	@Before
	public void setUp()
					throws Exception {

		super.setUp();
		mySqlOrderByPartGenerator = new MySqlOrderByPartGenerator();
		mySqlFieldGenerator = Mockito.mock(MySqlFieldGenerator.class);
		mySqlOrderByPartGenerator.setMySqlFieldGenerator(mySqlFieldGenerator);

		for(QuerySort sort: dataQueryDTO1.getSort()){
			Mockito.when(mySqlFieldGenerator.generateSql(sort.getField(), dataQueryDTO1)).thenReturn(sort.getField().getId());
		}
	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {

		String sqlStr = mySqlOrderByPartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "ORDER BY event_score DESC, event_time DESC";
		assertEquals("SQL order Part for DTO1" , expectedString, sqlStr);

	}
}
