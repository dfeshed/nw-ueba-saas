package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.DataQueryGeneratorTest;
import fortscale.services.dataqueries.querydto.QuerySort;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class MySqlOrderByPartGeneratorTest extends DataQueryGeneratorTest {

	@Before
	public void setUp()
					throws Exception {
        generator = new MySqlOrderByPartGenerator();

		super.setUp();
		for(QuerySort sort: dataQueryDTO1.getSort()){
			Mockito.when(mySqlFieldGenerator.generateSql(Mockito.eq(sort.getField()), Mockito.eq(dataQueryDTO1), Mockito.eq(false), Mockito.eq(false))).thenReturn(sort.getField().getId());;
		}
	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {

		String sqlStr = generator.generateQueryPart(dataQueryDTO1);
		String expectedString = "ORDER BY event_score DESC NULLS LAST , event_time DESC NULLS LAST ";
		assertEquals("SQL order Part for DTO1" , expectedString, sqlStr);

	}
}
