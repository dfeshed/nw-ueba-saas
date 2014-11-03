package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.querydto.DataQueryDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class MySqlWherePartGeneratorTest extends DataQueryGeneratorTest{

	private MySqlWherePartGenerator mySqlWherePartGenerator;


	@Before
	public void setUp()
			throws Exception {

		super.setUp();
		mySqlWherePartGenerator = new MySqlWherePartGenerator();
		mySqlWherePartGenerator.setDataEntitiesConfig(dataEntitiesConfig);
		mySqlWherePartGenerator.setMySqlUtils(mySqlUtils);


			for (DataQueryDTO.Term childTerm: dataQueryDTO1.conditions.terms){
				if (childTerm.getClass() == DataQueryDTO.ConditionField.class){
					DataQueryDTO.ConditionField condition = (DataQueryDTO.ConditionField)childTerm;
					Mockito.when(mySqlUtils.getConditionFieldSql(condition,dataQueryDTO1)).thenReturn(condition.field.getId() + "<=" + condition.getValue());
				}
			}

	}

	@Test
	public void testGenerateQueryPart()
			throws Exception {

		String sqlStr = mySqlWherePartGenerator.generateQueryPart(dataQueryDTO1);
		//TODO - check why this test is fail, it supposed to pass I believe it is something related to mockito
		String expectedString = "WHERE (event_score<=50 AND event_time_utc<=1414184400 AND event_time_utc<=1414360799)";
		assertEquals("SQL Where part for DTO1" , expectedString, sqlStr);

	}
}

