package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSplunk4768Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvt_splunk.conf";
	private String conf4768File = "resources/conf-files/processSecEvt4768.conf";

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
		morphlineTester.init(new String[] { confFile, conf4768File }, splunkSecEventsOutputFieldsExcludingEnrichment);
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
	    	"2014-03-21T23:24:58.000+02:00,2014-03-21 23:24:58,1395437098,tomerl,FORTSCALE.DOM,FORTSCALE\\tomerl,4768,192.168.100.157,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,false"
    		),
    		$ (
	        "Successfull 4768 Event over NAT",
	        "2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 	Account Name:		tomerl 	Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.0.22 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
	    	"2014-03-21T23:24:58.000+02:00,2014-03-21 23:24:58,1395437098,tomerl,FORTSCALE.DOM,FORTSCALE\\tomerl,4768,192.168.0.22,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,true,,,false"
    		),
    		$ (
	        "Failure 4768 Event",
	        "2014-03-19T11:03:34.000+02:00|03/19/2014 11:03:34 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4768	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248286113	Keywords=Audit Failure	Message=A Kerberos authentication ticket (TGT) was requested.		Account Information:		Account Name:		roees		Supplied Realm Name:	FORTSCALE		User ID:			NULL SID		Service Information:		Service Name:		krbtgt/FORTSCALE		Service ID:		NULL SID		Network Information:		Client Address:		::ffff:192.168.0.31		Client Port:		52813		Additional Information:		Ticket Options:		0x40810010		Result Code:		0x17		Ticket Encryption Type:	0xffffffff		Pre-Authentication Type:	-		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number:			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
	    	"2014-03-19T11:03:34.000+02:00,2014-03-19 11:03:34,1395219814,roees,FORTSCALE,NULL SID,4768,192.168.0.31,FAILURE,0x17,-,0x40810010,True,False,False,False,False,False,false,,,false"
    		),
    		$ (
	        "Event 4768 with no user name (Should be dropped)",
	        "2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 		Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.100.157 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
	    	null
    		),
    		$ (
    		"Regular 4768 Event",
    		"2014-02-24T13:38:40.000+02:00|02/24/2014 01:38:40 PM    LogName=Security        SourceName=Microsoft Windows security auditing. EventCode=4768  EventType=0     Type=Information        ComputerName=Fs-DC-01.Fortscale.dom     TaskCategory=Kerberos Authentication Service    OpCode=Info     RecordNumber=229771360  Keywords=Audit Success  Message=A Kerberos authentication ticket (TGT) was requested.           Account Information:            Account Name:           maxk            Supplied Realm Name:    FORTSCALE.DOM           User ID:                        FORTSCALEaxk            Service Information:            Service Name:          krbtgt          Service ID:             FORTSCALErbtgt          Network Information:            Client Address:         ::ffff:192.168.0.107            Client Port:            45665          Additional Information:         Ticket Options:         0x10            Result Code:            0x0             Ticket Encryption Type: 0x12            Pre-Authentication Type:       2               Certificate Information:                Certificate Issuer Name:                                Certificate Serial Number:                      Certificate Thumbprint:                        Certificate information is only provided if a certificate was used for pre-authentication.              Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
    		"2014-02-24T13:38:40.000+02:00,2014-02-24 13:38:40,1393241920,maxk,FORTSCALE.DOM,FORTSCALEaxk,4768,192.168.0.107,SUCCESS,0x0,2,0x10,False,False,False,False,False,False,false,,,false"
    		),
    		$ (
    		"4768 Event with computer as account name (Should be dropped)",
    		"2014-02-24T13:38:40.000+02:00|02/24/2014 01:38:40 PM    LogName=Security        SourceName=Microsoft Windows security auditing. EventCode=4768  EventType=0     Type=Information        ComputerName=Fs-DC-01.Fortscale.dom     TaskCategory=Kerberos Authentication Service    OpCode=Info     RecordNumber=229771360  Keywords=Audit Success  Message=A Kerberos authentication ticket (TGT) was requested.           Account Information:            Account Name:           maxk$            Supplied Realm Name:    FORTSCALE.DOM           User ID:                        FORTSCALEaxk            Service Information:            Service Name:          krbtgt          Service ID:             FORTSCALErbtgt          Network Information:            Client Address:         ::ffff:192.168.0.107            Client Port:            45665          Additional Information:         Ticket Options:         0x10            Result Code:            0x0             Ticket Encryption Type: 0x12            Pre-Authentication Type:       2               Certificate Information:                Certificate Issuer Name:                                Certificate Serial Number:                      Certificate Thumbprint:                        Certificate information is only provided if a certificate was used for pre-authentication.              Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
    		null
    		),
    		$ (
    		"Successfull 4768 Event with ' in the Account Name",
    		"2014-06-25T18:04:46.000+02:00|06/25/2014 06:04:46 PM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4768	EventType=0	Type=Information	ComputerName=DC01.corp.fortscale.com	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=4196374540	Keywords=Audit Success	Message=A Kerberos authentication ticket (TGT) was requested.		Account Information:		Account Name:		toy gangara's m		Supplied Realm Name:	CORP.FORTSCALE.COM		User ID:			CORP\\toy gangara's m		Service Information:		Service Name:		krbtgt		Service ID:		CORP\\krbtgt		Network Information:		Client Address:		10.148.172.23		Client Port:		60578		Additional Information:		Ticket Options:		0x40000000		Result Code:		0x0		Ticket Encryption Type:	0x12		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number:			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
    		"2014-06-25T18:04:46.000+02:00,2014-06-25 18:04:46,1403708686,toy gangaras m,CORP.FORTSCALE.COM,CORP\\toy gangaras m,4768,10.148.172.23,SUCCESS,0x0,2,0x40000000,True,False,False,False,False,False,false,,,false"
    		),
    		$(
    		"4624 event should be droped",
    		"2014-07-20T15:30:36.000+02:00|07/20/2014 03:30:36 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4624 EventType=0 Type=Information ComputerName=FS-DC-02.Fortscale.dom TaskCategory=Logon OpCode=Info RecordNumber=59222653 Keywords=Audit Success Message=An account was successfully logged on. Subject: 	Security ID:		NULL SID 	Account Name:		- 	Account Domain:		- 	Logon ID:		0x0 Logon Type:			3 New Logon: 	Security ID:		FORTSCALE\\ROEES-PC$ 	Account Name:		ROEES-PC$ 	Account Domain:		FORTSCALE 	Logon ID:		0x2805e1 	Logon GUID:		{2307381A-386D-053B-61E6-544ED755F9A8} Process Information: 	Process ID:		0x0 	Process Name:		-  Network Information: 	Workstation Name: 	Source Network Address:	192.168.0.158 	Source Port:		63514 Detailed Authentication Information: 	Logon Process:		Kerberos 	Authentication Package:	Kerberos 	Transited Services:	- 	Package Name (NTLM only):	- 	Key Length:		0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.  The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network). The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on. The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases. The authentication information fields provide detailed information about this specific logon request. 	- Logon GUID is a unique identifier that can be used to correlate this event with a KDC event. 	- Transited services indicate which intermediate services have participated in this logon request. 	- Package name indicates which sub-protocol was used among the NTLM protocols. 	- Key length indicates the length of the generated session key. This will be 0 if no session key was requested.", 
    		null
    		)
        );
 	}

}
