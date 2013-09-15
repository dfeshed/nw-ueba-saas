package fortscale.activedirectory.featureextraction;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

public class ADUserParserTest {

	ADUserParser adUserParser = new ADUserParser();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	
	@Test(expected=NullPointerException.class)
	public void testParseDate1() throws ParseException{
		adUserParser.parseDate(null);
	}
	
	@Test(expected=ParseException.class)
	public void testParseDate2() throws ParseException {
		adUserParser.parseDate("");
	}
	
	@Test(expected=ParseException.class)
	public void testParseDate3() throws ParseException {
		adUserParser.parseDate("0");
	}

	
	@SuppressWarnings("deprecation")
	@Test
	public void testParseDate4() {
		try {			
			Date date = adUserParser.parseDate("2013/08/22T16:45:17");
			assertTrue("Date Parser parses normal dates right", date.getYear()==113);
			assertTrue("Date Parser parses normal dates right", date.getMonth()==7);
			assertTrue("Date Parser parses normal dates right", date.getDate()==22);
			assertTrue("Date Parser parses normal dates right", date.getHours()==16);
			assertTrue("Date Parser parses normal dates right", date.getMinutes()==45);
			assertTrue("Date Parser parses normal dates right", date.getSeconds()==17);
		}
		catch (ParseException e) {
			fail("Date Parser fail on parsing normal dates");
		}
		
		
	}

	
	@Test
	public void testGetUserGroups() {
		assertTrue("Null memberOf returns empty array", adUserParser.getUserGroups(null).length==0);
		assertTrue("Empty memberOf returns empty array", adUserParser.getUserGroups("").length==0);
		
		String[] groups = adUserParser.getUserGroups("CN=Administrators,CN=Builtin,DC=Company");
		assertTrue("getUserGroups works good on normal input", groups.length==1);
		assertTrue("getUserGroups works good on normal input", groups[0].equals("CN=Administrators,CN=Builtin,DC=Company"));
		
		groups = adUserParser.getUserGroups("CN=Administrators,CN=Builtin,DC=Company;CN=VPN-Users,OU=Company-Users,DC=Company,DC=dom");
		assertTrue("getUserGroups works good on normal input", groups.length==2);
		assertTrue("getUserGroups works good on normal input", groups[0].equals("CN=Administrators,CN=Builtin,DC=Company"));
		assertTrue("getUserGroups works good on normal input", groups[1].equals("CN=VPN-Users,OU=Company-Users,DC=Company,DC=dom"));
	}

	@Test
	public void testGetAccountIsDisabledValue() {
		assertTrue("Account is disabled is recognized when needed", adUserParser.getAccountIsDisabledValue("514").equals(Feature.POSITIVE_STATUS));
		assertTrue("Account is disabled is unrecognized when needed", adUserParser.getAccountIsDisabledValue("512").equals(Feature.NEGATIVE_STATUS));
	}

	@Test
	public void testGetNoPasswordRequiresValue() {
		assertTrue("No Password Required is recognized when needed", adUserParser.getNoPasswordRequiresValue("544").equals(Feature.POSITIVE_STATUS));
		assertTrue("No Password Required is unrecognized when needed", adUserParser.getNoPasswordRequiresValue("512").equals(Feature.NEGATIVE_STATUS));
	}

	@Test
	public void testGetNormalUserAccountValue() {
		assertTrue("Normal User Account is recognized when needed", adUserParser.getNormalUserAccountValue("66048").equals(Feature.NEGATIVE_STATUS));
		assertTrue("Normal User Account is unrecognized when needed", adUserParser.getNormalUserAccountValue("2").equals(Feature.POSITIVE_STATUS));
	}

	@Test
	public void testGetInterdomainTrustAccountValue() {
		assertTrue("Interdomain Trust Account is recognized when needed", adUserParser.getInterdomainTrustAccountValue("2560").equals(Feature.POSITIVE_STATUS));
		assertTrue("Interdomain Trust Account is unrecognized when needed", adUserParser.getInterdomainTrustAccountValue("512").equals(Feature.NEGATIVE_STATUS));
	}

	@Test
	public void testGetPasswordNeverExpiresValue() {
		assertTrue("Password Never Expires is recognized when needed", adUserParser.getPasswordNeverExpiresValue("66048").equals(Feature.POSITIVE_STATUS));
		assertTrue("Password Never Expires is unrecognized when needed", adUserParser.getPasswordNeverExpiresValue("512").equals(Feature.NEGATIVE_STATUS));
	}

	@Test
	public void testGetDesKeyOnlyValue() {
		assertTrue("Des Key Only is recognized when needed", adUserParser.getDesKeyOnlyValue("2097664").equals(Feature.POSITIVE_STATUS));
		assertTrue("Des Key Only is unrecognized when needed", adUserParser.getDesKeyOnlyValue("512").equals(Feature.NEGATIVE_STATUS));
	}

	@Test
	public void testGetNumberOfSubordinates() {
		assertTrue("Null directReports return 0.0", 0.0 == adUserParser.getNumberOfSubordinates(null));
		assertTrue("Empty directReports return 0.0", 0.0 == adUserParser.getNumberOfSubordinates(""));
		
		assertTrue("getNumberOfSubordinates works good on normal input", 1.0 == adUserParser.getNumberOfSubordinates("CN=Subordinate1,OU=Company-Users,DC=Company,DC=dom"));
		assertTrue("getNumberOfSubordinates works good on normal input", 2.0 == adUserParser.getNumberOfSubordinates("CN=Subordinate1,OU=Company-Users,DC=Company,DC=dom;CN=Subordinate2,OU=Company-Users,DC=Company,DC=dom"));
	}

}
