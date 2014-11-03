package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.util.Assert;
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
		mySqlFromPartGenerator.setDataEntitiesConfig(dataEntitiesConfig);
	}

	@Test
	public void testGenerateQueryPart()
					throws Exception {


		String sqlStr = mySqlFromPartGenerator.generateQueryPart(dataQueryDTO1);
		String expectedString = "FROM someEntity";
		assertEquals("SQL From Part for DTO1" , expectedString, sqlStr);

	}
}
