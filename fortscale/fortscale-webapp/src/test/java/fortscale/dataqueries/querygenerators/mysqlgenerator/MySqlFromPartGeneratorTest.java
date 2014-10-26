package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.util.Assert;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class MySqlFromPartGeneratorTest extends DataQueryGeneratorTest{

	private MySqlFromPartGenerator mySqlFromPartGenerator;


	@Before
	public void setUp()
					throws Exception {

		super.setUp();

		mySqlFromPartGenerator = new MySqlFromPartGenerator();
		mySqlFromPartGenerator.setMySqlUtils(mySqlUtils);

	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {


		String sqlStr = mySqlFromPartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "lalala";
		assertEquals("SQL for DTO 1" , expectedString, sqlStr);


		sqlStr = mySqlFromPartGenerator.generateQueryPart(dataQueryDTO1);
		expectedString = "lalala";
		assertEquals("SQL for DTO 1" , expectedString, sqlStr);





	}
}
