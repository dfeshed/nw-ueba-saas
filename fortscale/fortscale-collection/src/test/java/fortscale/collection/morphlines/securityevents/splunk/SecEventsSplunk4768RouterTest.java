package fortscale.collection.morphlines.securityevents.splunk;

import static junitparams.JUnitParamsRunner.$;

import java.util.Arrays;
import java.util.List;

import fortscale.collection.morphlines.MorphlinesTester;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;





@RunWith(JUnitParamsRunner.class)
public class SecEventsSplunk4768RouterTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/splunk/readSecEvtRouter.conf";

	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/morphline-test-context-light.xml");
    }
	
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
		        "Successfull 4768 Event",
		        "2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 	Account Name:		tomerl 	Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.100.157 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
		    	"4768,false"
	    		),
	    		$ (
		        "Successfull 4768 Event over NAT",
		        "2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 	Account Name:		tomerl 	Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.0.22 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
		    	"4768,false"
	    		),
				$ (
				"Successfull 4768 Event - with WAN enrichment",
				"2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 	Account Name:		tomerl 	Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.100.157 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120. timezone Asia/Jerusalem datacenter Israel",
						"4768,false"
				),
	    		$ (
		        "Failure 4768 Event",
		        "2014-03-19T11:03:34.000+02:00|03/19/2014 11:03:34 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4768	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248286113	Keywords=Audit Failure	Message=A Kerberos authentication ticket (TGT) was requested.		Account Information:		Account Name:		roees		Supplied Realm Name:	FORTSCALE		User ID:			NULL SID		Service Information:		Service Name:		krbtgt/FORTSCALE		Service ID:		NULL SID		Network Information:		Client Address:		::ffff:192.168.0.31		Client Port:		52813		Additional Information:		Ticket Options:		0x40810010		Result Code:		0x17		Ticket Encryption Type:	0xffffffff		Pre-Authentication Type:	-		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number:			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
		    	"4768,false"
	    		),
	    		$ (
		        "Event 4768 with no user name",
		        "2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 		Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.100.157 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
		        null
	    		),
	    		$ (
	    		"Regular 4768 Event",
	    		"2014-02-24T13:38:40.000+02:00|02/24/2014 01:38:40 PM    LogName=Security        SourceName=Microsoft Windows security auditing. EventCode=4768  EventType=0     Type=Information        ComputerName=Fs-DC-01.Fortscale.dom     TaskCategory=Kerberos Authentication Service    OpCode=Info     RecordNumber=229771360  Keywords=Audit Success  Message=A Kerberos authentication ticket (TGT) was requested.           Account Information:            Account Name:           maxk            Supplied Realm Name:    FORTSCALE.DOM           User ID:                        FORTSCALEaxk            Service Information:            Service Name:          krbtgt          Service ID:             FORTSCALErbtgt          Network Information:            Client Address:         ::ffff:192.168.0.107            Client Port:            45665          Additional Information:         Ticket Options:         0x10            Result Code:            0x0             Ticket Encryption Type: 0x12            Pre-Authentication Type:       2               Certificate Information:                Certificate Issuer Name:                                Certificate Serial Number:                      Certificate Thumbprint:                        Certificate information is only provided if a certificate was used for pre-authentication.              Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
	    		"4768,false"
	    		),
	    		$ (
	    		"4768 Event with computer as account name",
	    		"2014-02-24T13:38:40.000+02:00|02/24/2014 01:38:40 PM    LogName=Security        SourceName=Microsoft Windows security auditing. EventCode=4768  EventType=0     Type=Information        ComputerName=Fs-DC-01.Fortscale.dom     TaskCategory=Kerberos Authentication Service    OpCode=Info     RecordNumber=229771360  Keywords=Audit Success  Message=A Kerberos authentication ticket (TGT) was requested.           Account Information:            Account Name:           maxk$            Supplied Realm Name:    FORTSCALE.DOM           User ID:                        FORTSCALEaxk            Service Information:            Service Name:          krbtgt          Service ID:             FORTSCALErbtgt          Network Information:            Client Address:         ::ffff:192.168.0.107            Client Port:            45665          Additional Information:         Ticket Options:         0x10            Result Code:            0x0             Ticket Encryption Type: 0x12            Pre-Authentication Type:       2               Certificate Information:                Certificate Issuer Name:                                Certificate Serial Number:                      Certificate Thumbprint:                        Certificate information is only provided if a certificate was used for pre-authentication.              Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
	    		"4768,true"
	    		),
	    		$ (
	    		"Successfull 4768 Event with ' in the Account Name",
	    		"2014-06-25T18:04:46.000+02:00|06/25/2014 06:04:46 PM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4768	EventType=0	Type=Information	ComputerName=DC01.corp.fortscale.com	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=4196374540	Keywords=Audit Success	Message=A Kerberos authentication ticket (TGT) was requested.		Account Information:		Account Name:		toy gangara's m		Supplied Realm Name:	CORP.FORTSCALE.COM		User ID:			CORP\\toy gangara's m		Service Information:		Service Name:		krbtgt		Service ID:		CORP\\krbtgt		Network Information:		Client Address:		10.148.172.23		Client Port:		60578		Additional Information:		Ticket Options:		0x40000000		Result Code:		0x0		Ticket Encryption Type:	0x12		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number:			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
	    		"4768,false"
	    		)
	        );
 	}


}
