package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.DataQueryUtils;
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

		ArrayList<DataQueryDTO.DataQueryField> fields = new ArrayList<DataQueryDTO.DataQueryField>();
		DataQueryDTO.DataQueryField field1 = new DataQueryDTO.DataQueryField();
		field1.setId("field1");
		fields.add(field1);

		DataQueryDTO.DataQueryField field2 = new DataQueryDTO.DataQueryField();
		field2.setId("field2");
		fields.add(field2);

		ArrayList<String> entities = new ArrayList<String>();
		entities.add(field1.getId());
		entities.add(field2.getId());

		Mockito.when(dataQueryUtils.getAllEntityFields(dataQueryDTO1.entities[0])).thenReturn(entities);
		for (DataQueryDTO.DataQueryField field : fields) {
			if (field != null)
				Mockito.when(mySqlUtils.getFieldSql(field, dataQueryDTO1, true)).thenReturn("aaa,ddd,eee");
		}

	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {
		String sqlStr = mySqlSelectPartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "SELECT field1, field2 ";
		assertEquals("SQL for DTO 1" , expectedString, sqlStr);

	}
}

