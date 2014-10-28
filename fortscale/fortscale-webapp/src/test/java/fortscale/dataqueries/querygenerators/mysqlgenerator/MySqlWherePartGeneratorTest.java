package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.DataQueryUtils;
import fortscale.dataqueries.querydto.DataQueryDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MySqlWherePartGeneratorTest extends DataQueryGeneratorTest{

	private MySqlWherePartGenerator mySqlWherePartGenerator;


	@Before
	public void setUp()
					throws Exception {

		super.setUp();
		mySqlWherePartGenerator = new MySqlWherePartGenerator();
		mySqlWherePartGenerator.setDataQueryUtils(dataQueryUtils);
		mySqlWherePartGenerator.setMySqlUtils(mySqlUtils);

		DataQueryDTO.ConditionField conditionField = new DataQueryDTO.ConditionField();
		conditionField.setValue("30");

		Mockito.when(mySqlUtils.getConditionFieldSql(conditionField,dataQueryDTO1)).thenReturn("field > 20");

	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {

		String sqlStr = mySqlWherePartGenerator.generateQueryPart(dataQueryDTO1);
		//TODO - check why this test is fail, it supposed to pass I believe it is something related to mockito
		String expectedString = "SELECT field1, field2 ";
		assertEquals("SQL for DTO 1" , expectedString, sqlStr);

	}
}

