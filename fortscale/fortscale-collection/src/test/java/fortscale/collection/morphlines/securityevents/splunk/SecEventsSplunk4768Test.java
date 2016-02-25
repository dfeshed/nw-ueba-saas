package fortscale.collection.morphlines.securityevents.splunk;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSplunk4768Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/splunk/readSecEvt.conf";
	private String conf4768File = "resources/conf-files/securityevents/splunk/processSecEvt4768.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

    @SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }


    @Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.security.events.login.table.morphline.fields");
		List<String> splunkSecEventsOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		List<String> splunkSecEventsOutputFieldsExcludingEnrichment = new ArrayList<>();
		for(String field: splunkSecEventsOutputFields){
			if(!field.equals("machine_name")){
				splunkSecEventsOutputFieldsExcludingEnrichment.add(field);
			}
		}
		morphlineTester.init(new String[] { confFile, conf4768File , confSecEnrich }, splunkSecEventsOutputFieldsExcludingEnrichment);
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
        return $(


				$(
						"Successfull 4768 Event",
						"2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 	Account Name:		tomerl 	Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.100.157 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"2014-03-21T23:24:58.000+02:00,2014-03-21 23:24:58,1395444298,tomerl,FORTSCALE.DOM,FORTSCALE\\tomerl,4768,192.168.100.157,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,,,,"
				),
				$(
						"Successfull 4768 Event - With WAN enrichment",
						"2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 	Account Name:		tomerl 	Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.100.157 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120. timezone Asia/Jerusalem datacenter Israel",
						"2014-03-21T23:24:58.000+02:00,2014-03-21 21:24:58,1395437098,tomerl,FORTSCALE.DOM,FORTSCALE\\tomerl,4768,192.168.100.157,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,,,,"
				),
				$(
						"Failure 4768 Event",
						"2014-03-19T11:03:34.000+02:00|03/19/2014 11:03:34 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4768	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248286113	Keywords=Audit Failure	Message=A Kerberos authentication ticket (TGT) was requested.		Account Information:		Account Name:		roees		Supplied Realm Name:	FORTSCALE		User ID:			NULL SID		Service Information:		Service Name:		krbtgt/FORTSCALE		Service ID:		NULL SID		Network Information:		Client Address:		::ffff:192.168.0.31		Client Port:		52813		Additional Information:		Ticket Options:		0x40810010		Result Code:		0x17		Ticket Encryption Type:	0xffffffff		Pre-Authentication Type:	-		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number:			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"2014-03-19T11:03:34.000+02:00,2014-03-19 11:03:34,1395227014,roees,FORTSCALE,NULL SID,4768,192.168.0.31,,FAILURE,0x17,-,0x40810010,True,False,False,False,False,False,false,,,,,,,"
				),
				$(
						"Event 4768 with no user name (Should be dropped)",
						"2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 		Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.100.157 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						null
				),
				$(
						"Regular 4768 Event",
						"2014-02-24T13:38:40.000+02:00|02/24/2014 01:38:40 PM    LogName=Security        SourceName=Microsoft Windows security auditing. EventCode=4768  EventType=0     Type=Information        ComputerName=Fs-DC-01.Fortscale.dom     TaskCategory=Kerberos Authentication Service    OpCode=Info     RecordNumber=229771360  Keywords=Audit Success  Message=A Kerberos authentication ticket (TGT) was requested.           Account Information:            Account Name:           maxk            Supplied Realm Name:    FORTSCALE.DOM           User ID:                        FORTSCALEaxk            Service Information:            Service Name:          krbtgt          Service ID:             FORTSCALErbtgt          Network Information:            Client Address:         ::ffff:192.168.0.107            Client Port:            45665          Additional Information:         Ticket Options:         0x10            Result Code:            0x0             Ticket Encryption Type: 0x12            Pre-Authentication Type:       2               Certificate Information:                Certificate Issuer Name:                                Certificate Serial Number:                      Certificate Thumbprint:                        Certificate information is only provided if a certificate was used for pre-authentication.              Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"2014-02-24T13:38:40.000+02:00,2014-02-24 13:38:40,1393249120,maxk,FORTSCALE.DOM,FORTSCALEaxk,4768,192.168.0.107,,SUCCESS,0x0,2,0x10,False,False,False,False,False,False,false,,,,,,,"
				),
				$(
						"4768 Event with computer as account name",
						"2014-02-24T13:38:40.000+02:00|02/24/2014 01:38:40 PM    LogName=Security        SourceName=Microsoft Windows security auditing. EventCode=4768  EventType=0     Type=Information        ComputerName=Fs-DC-01.Fortscale.dom     TaskCategory=Kerberos Authentication Service    OpCode=Info     RecordNumber=229771360  Keywords=Audit Success  Message=A Kerberos authentication ticket (TGT) was requested.           Account Information:            Account Name:           maxk$            Supplied Realm Name:    FORTSCALE.DOM           User ID:                        FORTSCALEaxk            Service Information:            Service Name:          krbtgt          Service ID:             FORTSCALErbtgt          Network Information:            Client Address:         ::ffff:192.168.0.107            Client Port:            45665          Additional Information:         Ticket Options:         0x10            Result Code:            0x0             Ticket Encryption Type: 0x12            Pre-Authentication Type:       2               Certificate Information:                Certificate Issuer Name:                                Certificate Serial Number:                      Certificate Thumbprint:                        Certificate information is only provided if a certificate was used for pre-authentication.              Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"2014-02-24T13:38:40.000+02:00,2014-02-24 13:38:40,1393249120,maxk$,FORTSCALE.DOM,FORTSCALEaxk,4768,192.168.0.107,,,0x0,2,0x10,False,False,False,False,False,False,false,,,,,,,"
				),
				$(
						"Successfull 4768 Event with ' in the Account Name",
						"2014-06-25T18:04:46.000+02:00|06/25/2014 06:04:46 PM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4768	EventType=0	Type=Information	ComputerName=DC01.corp.fortscale.com	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=4196374540	Keywords=Audit Success	Message=A Kerberos authentication ticket (TGT) was requested.		Account Information:		Account Name:		toy gangara's m		Supplied Realm Name:	CORP.FORTSCALE.COM		User ID:			CORP\\toy gangara's m		Service Information:		Service Name:		krbtgt		Service ID:		CORP\\krbtgt		Network Information:		Client Address:		10.148.172.23		Client Port:		60578		Additional Information:		Ticket Options:		0x40000000		Result Code:		0x0		Ticket Encryption Type:	0x12		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number:			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"2014-06-25T18:04:46.000+02:00,2014-06-25 18:04:46,1403719486,toy gangaras m,CORP.FORTSCALE.COM,CORP\\toy gangaras m,4768,10.148.172.23,,SUCCESS,0x0,2,0x40000000,True,False,False,False,False,False,false,,,,,,,"
				)

		);
 	}

}
