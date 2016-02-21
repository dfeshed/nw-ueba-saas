package fortscale.collection.morphlines.securityevents.splunk;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSplunk4771Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/splunk/readSecEvt.conf";
	private String conf4771File = "resources/conf-files/securityevents/splunk/processSecEvt4771.conf";
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
		morphlineTester.init(new String[] { confFile, conf4771File,confSecEnrich }, splunkSecEventsOutputFieldsExcludingEnrichment);
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
	        "4771 Event with all neccessary fields",
	        "2014-03-19T10:38:53.000+02:00|03/19/2014 10:38:53 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4771	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248270818	Keywords=Audit Failure	Message=Kerberos pre-authentication failed.		Account Information:		Security ID:		FORTSCALEaxk		Account Name:		maxk		Service Information:		Service Name:		krbtgt/FORTSCALE.DOM		Network Information:		Client Address:		::ffff:192.168.0.107		Client Port:		55612		Additional Information:		Ticket Options:		0x40800000		Failure Code:		0x18		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number: 			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options and failure codes are defined in RFC 4120.		If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present.",
	    	"2014-03-19T10:38:53.000+02:00,2014-03-19 10:38:53,1395225533,maxk,FORTSCALE.DOM,FORTSCALEaxk,4771,192.168.0.107,,FAILURE,0x18,2,0x40800000,True,False,False,False,False,False,false,,,,,,,"
    		),
			$ (
					"4771 Event with all neccessary fields - With WAN enrichment",
					"2014-03-19T10:38:53.000+02:00|03/19/2014 10:38:53 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4771	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248270818	Keywords=Audit Failure	Message=Kerberos pre-authentication failed.		Account Information:		Security ID:		FORTSCALEaxk		Account Name:		maxk		Service Information:		Service Name:		krbtgt/FORTSCALE.DOM		Network Information:		Client Address:		::ffff:192.168.0.107		Client Port:		55612		Additional Information:		Ticket Options:		0x40800000		Failure Code:		0x18		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number: 			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options and failure codes are defined in RFC 4120.		If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present.  Flume enrichment timezone Asia/Jerusalem",
					"2014-03-19T10:38:53.000+02:00,2014-03-19 08:38:53,1395218333,maxk,FORTSCALE.DOM,FORTSCALEaxk,4771,192.168.0.107,,FAILURE,0x18,2,0x40800000,True,False,False,False,False,False,false,,,,,,,"
			),
    		$ (
	        "4771 Event with computer as account name (Should be dropped)",
	        "2014-03-19T10:38:53.000+02:00|03/19/2014 10:38:53 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4771	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248270818	Keywords=Audit Failure	Message=Kerberos pre-authentication failed.		Account Information:		Security ID:		FORTSCALEaxk		Account Name:		maxk$		Service Information:		Service Name:		krbtgt/FORTSCALE.DOM		Network Information:		Client Address:		::ffff:192.168.0.22		Client Port:		55612		Additional Information:		Ticket Options:		0x40800000		Failure Code:		0x18		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number: 			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options and failure codes are defined in RFC 4120.		If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present.",
	    	null
    		),
    		$ (
	        "Event 4771 with no user name (Should be dropped)",
	        "2014-03-19T10:38:53.000+02:00|03/19/2014 10:38:53 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4771	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248270818	Keywords=Audit Failure	Message=Kerberos pre-authentication failed.		Account Information:		Security ID:		FORTSCALEaxk		Service Information:		Service Name:		krbtgt/FORTSCALE.DOM		Network Information:		Client Address:		::ffff:192.168.0.107		Client Port:		55612		Additional Information:		Ticket Options:		0x40800000		Failure Code:		0x18		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number: 			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options and failure codes are defined in RFC 4120.		If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present.	",
	    	null
    		),
    		$(
    		"4624 event should be droped",
    		"2014-07-20T15:30:36.000+02:00|07/20/2014 03:30:36 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4624 EventType=0 Type=Information ComputerName=FS-DC-02.Fortscale.dom TaskCategory=Logon OpCode=Info RecordNumber=59222653 Keywords=Audit Success Message=An account was successfully logged on. Subject: 	Security ID:		NULL SID 	Account Name:		- 	Account Domain:		- 	Logon ID:		0x0 Logon Type:			3 New Logon: 	Security ID:		FORTSCALE\\ROEES-PC$ 	Account Name:		ROEES-PC$ 	Account Domain:		FORTSCALE 	Logon ID:		0x2805e1 	Logon GUID:		{2307381A-386D-053B-61E6-544ED755F9A8} Process Information: 	Process ID:		0x0 	Process Name:		-  Network Information: 	Workstation Name: 	Source Network Address:	192.168.0.158 	Source Port:		63514 Detailed Authentication Information: 	Logon Process:		Kerberos 	Authentication Package:	Kerberos 	Transited Services:	- 	Package Name (NTLM only):	- 	Key Length:		0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.  The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network). The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on. The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases. The authentication information fields provide detailed information about this specific logon request. 	- Logon GUID is a unique identifier that can be used to correlate this event with a KDC event. 	- Transited services indicate which intermediate services have participated in this logon request. 	- Package name indicates which sub-protocol was used among the NTLM protocols. 	- Key length indicates the length of the generated session key. This will be 0 if no session key was requested.", 
    		null
    		)
        );
 	}

}
