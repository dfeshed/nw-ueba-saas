package fortscale.dataqueries.querygenerators.mysqlgenerator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MySqlLimitPartGeneratorTest extends DataQueryGeneratorTest{

	private MySqlLimitPartGenerator mySqlLimitPartGenerator;


	@Before
	public void setUp()
					throws Exception {

		super.setUp();
		mySqlLimitPartGenerator = new MySqlLimitPartGenerator();
	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {

		String sqlStr = mySqlLimitPartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "LIMIT 20";
		assertEquals("SQL Limit Part for DTO 1" , expectedString, sqlStr);

	}
}
