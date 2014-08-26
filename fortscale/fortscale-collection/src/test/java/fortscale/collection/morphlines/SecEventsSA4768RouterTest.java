package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.Arrays;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
public class SecEventsSA4768RouterTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvtRouter_securityAnalytics.conf";

	
	@Before
	public void setUp() throws Exception {
		List<String> fieldsToCheck = Arrays.asList("eventCode","isComputer");
		morphlineTester.init(new String[] { confFile}, fieldsToCheck);
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
	        "Successful 4768 Event Type 1",
	        "Mar 17 15:49:21 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.#177#177Account Information:#177Account Name:avi_m#177Supplied Realm Name:il#177User ID:#177S-1-5-21-2289726844-590661003-2420928919-2295#177#177Service Information:#177Service Name:krbtgt#177Service ID:#177S-1-5-21-2289726844-590661003-2420928919-502#177#177Network Information:#177Client Address:#177::ffff:192.168.158.226#177Client Port:56284#177#177Additional Information:#177Ticket Options:0x40810010#177Result Code:0x0#177Ticket Encryption Type:0x12#177Pre-Authentication Type:2#177#177Certificate Information:#177Certificate Issuer Name:#177Certificate Serial Number:#177Certificate Thumbprint:#177#177Certificate information is only provided if a certificate#177was used for pre-authentication.#177#177Pre-authentication types, ticket options, encryption types#177and result codes are defined in RFC 4120. ",
	    	"4768,false"
    		),
    		
    		$ (
	        "Failure 4768 Event Type 1",
	        "Mar 17 15:49:28 IL-DC2 microsoft-windows-security-auditing[failure] 4768 A Kerberos authentication ticket (TGT) was requested.#177#177Account Information:#177Account Name:alon_b#177Supplied Realm Name:IL#177User ID:S-1-0-0#177#177Service Information:#177Service Name:krbtgt/IL#177Service ID:S-1-0-0#177#177Network Information:#177Client Address:#177::ffff:192.168.158.38#177Client Port:51496#177#177Additional Information:#177Ticket Options:0x40810010#177Result Code:0x6#177Ticket Encryption Type:0xffffffff#177Pre-Authentication Type:-#177#177Certificate Information:#177Certificate Issuer Name:#177Certificate Serial Number:#177Certificate Thumbprint:#177#177Certificate information is only provided if a certificate#177was used for pre-authentication.#177#177Pre-authentication types, ticket options, encryption types#177and result codes are defined in RFC 4120. ",
	    	"4768,false"
    		),
    		
        	$ (
        	"Successful 4768 Event Type 2",
        	"May  1 10:14:31 roee-hd5 %NICWIN-4-Security_4768_Microsoft-Windows-Security-Auditing: Security,rn=462908116 cid=6992 eid=560,Mon Mar 17 13:48:37 2014,4768,Microsoft-Windows-Security-Auditing,,Audit Success,BG-DC2.bg.playtech.corp,Kerberos Authentication Service,,A Kerberos authentication ticket (TGT) was requested.  Account Information:  Account Name:  deannek  Supplied Realm Name: bg  User ID:   S-1-5-21-3421828858-1269048617-336047487-1435  Service Information:  Service Name:  krbtgt  Service ID:  S-1-5-21-3421828858-1269048617-336047487-502  Network Information:  Client Address:  ::ffff:192.168.203.147  Client Port:  50670  Additional Information:  Ticket Options:  0x40810010  Result Code:  0x0  Ticket Encryption Type: 0x12  Pre-Authentication Type: 2  Certificate Information:  Certificate Issuer Name:    Certificate Serial Number:   Certificate Thumbprint:    Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
        	"4768,false"
        	),

    		$ (
	        "Successful 4768 Event Type 1 with ' in Account Name",
	        "Mar 17 15:49:21 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.#177#177Account Information:#177Account Name:avi'm#177Supplied Realm Name:il#177User ID:#177S-1-5-21-2289726844-590661003-2420928919-2295#177#177Service Information:#177Service Name:krbtgt#177Service ID:#177S-1-5-21-2289726844-590661003-2420928919-502#177#177Network Information:#177Client Address:#177::ffff:192.168.158.226#177Client Port:56284#177#177Additional Information:#177Ticket Options:0x40810010#177Result Code:0x0#177Ticket Encryption Type:0x12#177Pre-Authentication Type:2#177#177Certificate Information:#177Certificate Issuer Name:#177Certificate Serial Number:#177Certificate Thumbprint:#177#177Certificate information is only provided if a certificate#177was used for pre-authentication.#177#177Pre-authentication types, ticket options, encryption types#177and result codes are defined in RFC 4120. ",
	    	"4768,false"
    		),

        	$ (
        	"Successful 4768 Event Type 2 with ' in Account Name",
        	"May  1 10:14:31 roee-hd5 %NICWIN-4-Security_4768_Microsoft-Windows-Security-Auditing: Security,rn=462908116 cid=6992 eid=560,Mon Mar 17 13:48:37 2014,4768,Microsoft-Windows-Security-Auditing,,Audit Success,BG-DC2.bg.playtech.corp,Kerberos Authentication Service,,A Kerberos authentication ticket (TGT) was requested.  Account Information:  Account Name:  deanne'k  Supplied Realm Name: bg  User ID:   S-1-5-21-3421828858-1269048617-336047487-1435  Service Information:  Service Name:  krbtgt  Service ID:  S-1-5-21-3421828858-1269048617-336047487-502  Network Information:  Client Address:  ::ffff:192.168.203.147  Client Port:  50670  Additional Information:  Ticket Options:  0x40810010  Result Code:  0x0  Ticket Encryption Type: 0x12  Pre-Authentication Type: 2  Certificate Information:  Certificate Issuer Name:    Certificate Serial Number:   Certificate Thumbprint:    Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
        	"4768,false"
        	)
	        );
 	}


}
