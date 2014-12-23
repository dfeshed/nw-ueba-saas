package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.DataQueryGeneratorTest;
import fortscale.services.dataqueries.querydto.DataQueryField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MySqlSelectPartGeneratorTest extends DataQueryGeneratorTest {

	@Before
	public void setUp()
					throws Exception {
        generator = new MySqlSelectPartGenerator();

		super.setUp();

		ArrayList<String> fields = new ArrayList<String>();
		fields.add("field1");
		fields.add("field2");

		Mockito.when(dataEntitiesConfig.getAllEntityFields(Mockito.eq(dataQueryDTO1.getEntities()[0]))).thenReturn(fields);
		Mockito.when(mySqlFieldGenerator.generateSql(Mockito.any(DataQueryField.class), Mockito.eq(dataQueryDTO1), Mockito.eq(true))).thenReturn("someField");

	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {
		String sqlStr = generator.generateQueryPart(dataQueryDTO1);
		String expectedString = "SELECT someField, someField";
		assertEquals("SQL Select Part for DTO1" , expectedString, sqlStr);
		Mockito.verify(dataEntitiesConfig).getAllEntityFields("kerberos_logins");
	}
}

