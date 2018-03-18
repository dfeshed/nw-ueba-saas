package fortscale.utils;

import static org.junit.Assert.*;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class ConfigurationUtilsTest {

	@Test
	@Parameters({"", "null"})
	public void checkEmptyInput(String value) {
		if (value.equals("null"))
			value = null;
		
		String[][] result = ConfigurationUtils.getStringArrays(value);
		
		assertNotNull(result);
		assertArrayEquals(new String[0][0], result);
	}
	
	@Test
	@Parameters({ 
		"abs, 1, 1",
		"aaaa#####ccc, 2, 1",
		"([\\S]+)@([^ \\t\\n\\x0B\\f\\r\\.]+)\\.([^ \\t\\n\\x0B\\f\\r\\.]+)# # #$1@$2.$3#####([^ \\t\\n\\x0B\\f\\r\\@]+)# # #$1@fortscale.com#####([\\S]+)@[^ \\t\\n\\x0B\\f\\r]+\\.([^ \\t\\n\\x0B\\f\\r\\.]+)\\.([^ \\t\\n\\x0B\\f\\r\\.]+)# # #$1@$2.$3, 3, 2"
		})
	public void checkSplit(String value, int rows, int cells) {
		
		String[][] result = ConfigurationUtils.getStringArrays(value);
		
		assertNotNull(result);
		assertEquals(rows, result.length);
		for (String[] line : result)
			assertEquals(cells, line.length);
	}
	
}
