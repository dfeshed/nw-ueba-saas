package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.DataQueryGeneratorTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MySqlLimitPartGeneratorTest extends DataQueryGeneratorTest {

	@Before
	public void setUp()
					throws Exception {
        generator = new MySqlLimitPartGenerator();
		super.setUp();
	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {
		String sqlStr = generator.generateQueryPart(dataQueryDTO1);
		String expectedString = "LIMIT 20";
		assertEquals("SQL Limit Part for DTO 1" , expectedString, sqlStr);
	}

	@Test
	public void testGenerateQueryPart_offset()
			throws Exception {
		dataQueryDTO1.setOffset(4);
		dataQueryDTO1.setLimit(70);
		String sqlStr = generator.generateQueryPart(dataQueryDTO1);
		String expectedString = "LIMIT 70 OFFSET 4";
		assertEquals("SQL Limit and offset Part for DTO 1" , expectedString, sqlStr);

	}
}
