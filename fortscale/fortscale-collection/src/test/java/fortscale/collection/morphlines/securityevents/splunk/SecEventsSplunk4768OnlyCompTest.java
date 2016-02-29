package fortscale.collection.morphlines.securityevents.splunk;

import fortscale.collection.morphlines.MorphlinesTester;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSplunk4768OnlyCompTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/splunk/readSecEvtOnlyComp.conf";
	private String conf4768File = "resources/conf-files/securityevents/splunk/read4768SecEvtOnlyComp.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

	
	
	
	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }
	
	@Before
	public void setUp() throws Exception {
		List<String> fieldsToCheck = Arrays.asList("date_time_unix","client_address","account_name","account_domain","reporting_server");
		morphlineTester.init(new String[] { confFile, conf4768File,confSecEnrich }, fieldsToCheck);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}
	
	@Test
	@Parameters
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@SuppressWarnings("unused")
 	private Object[] parametersForTest() {
        return	$(
    		$ (
    		"4768 Event with computer as account name (Should be dropped)",
    		"2014-02-24T13:38:40.000+02:00|02/24/2014 01:38:40 PM    LogName=Security        SourceName=Microsoft Windows security auditing. EventCode=4768  EventType=0     Type=Information        ComputerName=Fs-DC-01.Fortscale.dom     TaskCategory=Kerberos Authentication Service    OpCode=Info     RecordNumber=229771360  Keywords=Audit Success  Message=A Kerberos authentication ticket (TGT) was requested.           Account Information:            Account Name:           maxk$            Supplied Realm Name:    FORTSCALE.DOM           User ID:                        FORTSCALEaxk            Service Information:            Service Name:          krbtgt          Service ID:             FORTSCALErbtgt          Network Information:            Client Address:         ::ffff:192.168.0.107            Client Port:            45665          Additional Information:         Ticket Options:         0x10            Result Code:            0x0             Ticket Encryption Type: 0x12            Pre-Authentication Type:       2               Certificate Information:                Certificate Issuer Name:                                Certificate Serial Number:                      Certificate Thumbprint:                        Certificate information is only provided if a certificate was used for pre-authentication.              Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
    		"1393249120,192.168.0.107,maxk$,FORTSCALE.DOM,Fs-DC-01.Fortscale.dom"
    		),
			$ (
					"4768 Event with another | sign at the date",
					"2016-01-10T13:21:01.000+03|:00|2016-01-10 13:21:01 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=752906791 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested. Account Information: Account Name: secusr14_SRV$ Supplied Realm Name: somebigcompany.com User ID: somebigcompany secusr10_SRV Service Information: Service Name: krbtgt Service ID: FORTSCALE krbtgt Network Information: Client Address: ::ffff:192.168.20.20 Client Port: 58730 Additional Information: Ticket Options: 0x40810010 Result Code: NO VALUE Ticket Encryption Type: 0x12 Pre-Authentication Type: 2 Certificate Information: Certificate Issuer Name:",
					null
			)
        );
 	}

}
