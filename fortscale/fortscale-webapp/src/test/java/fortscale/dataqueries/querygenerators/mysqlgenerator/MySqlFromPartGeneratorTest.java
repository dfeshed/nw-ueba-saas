package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.querydto.ConditionField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class MySqlFromPartGeneratorTest extends DataQueryGeneratorTest{

	private MySqlFromPartGenerator mySqlFromPartGenerator;


	@Before
	public void setUp()
					throws Exception {

		super.setUp();
		mySqlFromPartGenerator = new MySqlFromPartGenerator();

		mySqlFromPartGenerator.setDataEntitiesConfig(dataEntitiesConfig);

		Mockito.when(dataEntitiesConfig.getEntityPerformanceTable(dataQueryDTO1.getEntities()[0])).thenReturn("ldapauth_top");
		Mockito.when(dataEntitiesConfig.getEntityTable(dataQueryDTO1.getEntities()[0])).thenReturn("ldapauth");
		Mockito.when(dataEntitiesConfig.getEntityPerformanceTableField(dataQueryDTO1.getEntities()[0])).thenReturn("event_score");
		Mockito.when(dataEntitiesConfig.getEntityPerformanceTableFieldMinValue(dataQueryDTO1.getEntities()[0])).thenReturn(50);
	}
	@Test
	public void testGenerateQueryPart_top()
					throws Exception {

		String sqlStr = mySqlFromPartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "FROM ldapauth_top";
		assertEquals("SQL From Part for DTO1 (top)" , expectedString, sqlStr);

	}

	@Test
	public void testGenerateQueryPart()
			throws Exception {

		((ConditionField) dataQueryDTO1.getConditions().getTerms().get(0)).setValue("10");
		String sqlStr = mySqlFromPartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "FROM ldapauth";
		assertEquals("SQL From Part for DTO1" , expectedString, sqlStr);

	}


}
