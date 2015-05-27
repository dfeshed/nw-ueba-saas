package fortscale.utils.actdir;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class ADParserTest {

	public ADParser parser  = new ADParser();


	@Test
	public void testParseDCFromDN() throws Exception {

		String dn = "CN=Admin_PC_1,CN=Computers,DC=somebigcompany,DC=com";

		String DC = parser.parseDCFromDN(dn);

		assertTrue(DC.equals("somebigcompany.com"));

	}

	@Test
	public void testParseMissedDCFromDN() throws Exception {

		String dn = "CN=Admin_PC_1,CN=Computers";

		String DC = parser.parseDCFromDN(dn);

		assertTrue(DC.equals(""));

	}
}
