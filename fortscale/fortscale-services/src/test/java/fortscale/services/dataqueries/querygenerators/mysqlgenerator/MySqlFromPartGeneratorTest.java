package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.DataQueryGeneratorTest;
import fortscale.services.dataqueries.querydto.ConditionField;
import fortscale.services.dataqueries.querydto.MultipleDataQueryDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class MySqlFromPartGeneratorTest extends DataQueryGeneratorTest {

    MySqlFromPartGenerator generator;
    MySqlMultipleQueryGenerator mySqlMultipleQueryGenerator;

	@Before
	public void setUp()
					throws Exception {
        generator = new MySqlFromPartGenerator();

        super.setUp();

        generator.setMySqlFieldGenerator(mySqlFieldGenerator);
        generator.setDataEntitiesConfig(dataEntitiesConfig);
        generator.setDataQueryDtoHelper(dataQueryDtoHelper);
        mySqlMultipleQueryGenerator = Mockito.mock(MySqlMultipleQueryGenerator.class);
        generator.setMySqlMultipleQueryGenerator(mySqlMultipleQueryGenerator);


		Mockito.when(dataEntitiesConfig.getEntityPerformanceTable(dataQueryDTO1.getEntities()[0])).thenReturn("ldapauth_top");
		Mockito.when(dataEntitiesConfig.getEntityTable(dataQueryDTO1.getEntities()[0])).thenReturn("ldapauth");
		Mockito.when(dataEntitiesConfig.getEntityPerformanceTableField(dataQueryDTO1.getEntities()[0])).thenReturn("event_score");
		Mockito.when(dataEntitiesConfig.getEntityPerformanceTableFieldMinValue(dataQueryDTO1.getEntities()[0])).thenReturn(50);
        Mockito.when(mySqlMultipleQueryGenerator.generateQueryPart(Mockito.any(MultipleDataQueryDTO.class))).thenReturn("[query1] UNION [query2]");
        Mockito.when(mySqlMultipleQueryGenerator.getSubQuerySql(Mockito.any(MultipleDataQueryDTO.class))).thenReturn("([query1] UNION [query2]) as t1");

    }
	@Test
	public void testGenerateQueryPart_top()
					throws Exception {

		String sqlStr = generator.generateQueryPart(dataQueryDTO1);
		String expectedString = "FROM ldapauth_top as ldapauth";
		assertEquals("SQL From Part for DTO1 (top)" , expectedString, sqlStr);

	}

	@Test
	public void testGenerateQueryPart()
			throws Exception {

		((ConditionField) dataQueryDTO1.getConditions().getTerms().get(0)).setValue("10");
		String sqlStr = generator.generateQueryPart(dataQueryDTO1);
		String expectedString = "FROM ldapauth";
		assertEquals("SQL From Part for DTO1" , expectedString, sqlStr);

	}

    @Test
    public void testFromWithSubQuery() throws Exception{
        String sqlStr = generator.generateQueryPart(dataQueryDto_UnionDistinct);
        String expectedString = "FROM ([query1] UNION [query2]) as t1";
        assertEquals("SQL From Part with subQuery" , expectedString, sqlStr);
    }
}
