package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.querydto.DataQueryDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MySqlSelectPartGeneratorTest extends DataQueryGeneratorTest{

	private MySqlSelectPartGenerator mySqlSelectPartGenerator;


	@Before
	public void setUp()
					throws Exception {

		super.setUp();
		mySqlSelectPartGenerator = new MySqlSelectPartGenerator();
		mySqlSelectPartGenerator.setDataQueryUtils(dataQueryUtils);
		mySqlSelectPartGenerator.setMySqlUtils(mySqlUtils);

		String fieldName_1 = "field1";
		DataQueryDTO.DataQueryField field1 = new DataQueryDTO.DataQueryField();
		field1.setId(fieldName_1);

		String fieldName_2 = "field2";
		DataQueryDTO.DataQueryField field2 = new DataQueryDTO.DataQueryField();
		field2.setId(fieldName_2);


		ArrayList<String> fields = new ArrayList<String>();
		fields.add(fieldName_1);
		fields.add(fieldName_2);

		Mockito.when(dataQueryUtils.getAllEntityFields(Mockito.any(String.class))).thenReturn(fields);
		Mockito.when(mySqlUtils.getFieldSql(field1,dataQueryDTO1,true)).thenReturn(fieldName_1);
		Mockito.when(mySqlUtils.getFieldSql(field2,dataQueryDTO1,true)).thenReturn(fieldName_2);

	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {

		String sqlStr = mySqlSelectPartGenerator.generateQueryPart(dataQueryDTO1);
		//TODO - check why this test is fail, it supposed to pass I believe it is something related to mockito
		String expectedString = "SELECT field1, field2 ";
		assertEquals("SQL for DTO 1" , expectedString, sqlStr);

	}
}

