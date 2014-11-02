package fortscale.dataqueries.querygenerators.mysqlgenerator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class MySqlOrderByPartGeneratorTest extends DataQueryGeneratorTest{

	private MySqlOrderByPartGenerator mySqlOrderByPartGenerator;


	@Before
	public void setUp()
					throws Exception {

		super.setUp();
		mySqlOrderByPartGenerator = new MySqlOrderByPartGenerator();
		mySqlOrderByPartGenerator.setMySqlUtils(mySqlUtils);

		Mockito.when(mySqlUtils.getFieldSql(dataQueryDTO1.sort.get(0).field,dataQueryDTO1)).thenReturn("fieldToSortBy1");
		Mockito.when(mySqlUtils.getFieldSql(dataQueryDTO1.sort.get(1).field,dataQueryDTO1)).thenReturn("fieldToSortBy2");

	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {

		String sqlStr = mySqlOrderByPartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "ORDER BY fieldToSortBy1 DESC, fieldToSortBy2 DESC";
		assertEquals("SQL order Part for DTO1" , expectedString, sqlStr);

	}
}
