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
public class SecEventsSplunk4769Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/splunk/readSecEvt.conf";
	private String conf4769File = "resources/conf-files/securityevents/splunk/processSecEvt4769.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

	
	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }
	
	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.kerberos_logins.table.morphline.fields");
		List<String> splunkSecEventsOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		List<String> splunkSecEventsOutputFieldsExcludingEnrichment = new ArrayList<>();
		for(String field: splunkSecEventsOutputFields){
			//if(!field.equals("machine_name")){
				splunkSecEventsOutputFieldsExcludingEnrichment.add(field);
			//}
		}
		morphlineTester.init(new String[] { confFile, conf4769File,confSecEnrich }, splunkSecEventsOutputFieldsExcludingEnrichment);
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
		        "Successfull 4769 Event",
		    	"2014-03-21T23:24:58.000+02:00|03/21/2014 11:24:58 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4769 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Service Ticket Operations OpCode=Info RecordNumber=249579343 Keywords=Audit Success Message=A Kerberos service ticket was requested. Account Information: Account Name:	 roees@FORTSCALE.DOM Account Domain:	 FORTSCALE.DOM Logon GUID:	 {99448FA4-399E-D675-CD76-9212248B50D8} Service Information: Service Name:	 FS-DC-01$ Service ID:	 FORTSCALE\\FS-DC-01$ Network Information: Client Address:	 ::ffff:192.168.100.141 Client Port:	 58076 Additional Information: Ticket Options:	 0x40810000 Ticket Encryption Type:	0x12 Failure Code:	 0x0 Transited Services:	- This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	"2014-03-21T23:24:58.000+02:00,2014-03-21 23:24:58,Kerberos Service Ticket Operations,4769,Security,249579343,Microsoft Windows security auditing.,roees@FORTSCALE.DOM,FORTSCALE.DOM,FS-DC-01,FORTSCALE\\FS-DC-01$,192.168.100.141,0x40810000,0x0,,1395444298,,false,,,,,,,,,"
        		),
				$ (
						"Successfull 4769 Event - With WAN enrichment",
						"2014-03-21T23:24:58.000+02:00|03/21/2014 11:24:58 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4769 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Service Ticket Operations OpCode=Info RecordNumber=249579343 Keywords=Audit Success Message=A Kerberos service ticket was requested. Account Information: Account Name:	 roees@FORTSCALE.DOM Account Domain:	 FORTSCALE.DOM Logon GUID:	 {99448FA4-399E-D675-CD76-9212248B50D8} Service Information: Service Name:	 FS-DC-01$ Service ID:	 FORTSCALE\\FS-DC-01$ Network Information: Client Address:	 ::ffff:192.168.100.141 Client Port:	 58076 Additional Information: Ticket Options:	 0x40810000 Ticket Encryption Type:	0x12 Failure Code:	 0x0 Transited Services:	- This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.  Flume enrichment timezone Asia/Jerusalem",
						"2014-03-21T23:24:58.000+02:00,2014-03-21 21:24:58,Kerberos Service Ticket Operations,4769,Security,249579343,Microsoft Windows security auditing.,roees@FORTSCALE.DOM,FORTSCALE.DOM,FS-DC-01,FORTSCALE\\FS-DC-01$,192.168.100.141,0x40810000,0x0,,1395437098,,false,,,,,,,,,"
				),
        		$ (
        		"Successfull 4769 Event with Service Name that is without $",
		        "2014-06-17T09:00:00.000-04:00|06/17/2014 09:00:00 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4769	EventType=0	Type=Information	ComputerName=TLVXDCP01.Fortscale.com	TaskCategory=Kerberos Service Ticket Operations	OpCode=Info	RecordNumber=97628557	Keywords=Audit Success	Message=A Kerberos service ticket was requested.		Account Information:		Account Name:		IL-SP-P-DARFAMM@FORTSCALE.COM		Account Domain:		FORTSCALE.COM		Logon GUID:		{7160E105-C605-2936-2B43-B3D49C5EED29}		Service Information:		Service Name:		IL-SP-P-DARFAMM		Service ID:		TLV_DOMAIN\\IL-SP-P-DARFAMM		Network Information:		Client Address:		::ffff:172.16.28.178		Client Port:		61285		Additional Information:		Ticket Options:		0x40810008		Ticket Encryption Type:	0x12		Failure Code:		0x0		Transited Services:	-		This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.		This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.		Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	"2014-06-17T09:00:00.000-04:00,2014-06-17 09:00:00,Kerberos Service Ticket Operations,4769,Security,97628557,Microsoft Windows security auditing.,IL-SP-P-DARFAMM@FORTSCALE.COM,FORTSCALE.COM,IL-SP-P-DARFAMM,TLV_DOMAIN\\IL-SP-P-DARFAMM,172.16.28.178,0x40810008,0x0,,1402995600,,false,,,,,,,,,"
        		),
        		$ (
		        "4769 Event with computer as account name (Should be dropped)",
		    	"2014-03-21T23:24:58.000+02:00|03/21/2014 11:24:58 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4769 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Service Ticket Operations OpCode=Info RecordNumber=249579343 Keywords=Audit Success Message=A Kerberos service ticket was requested. Account Information: Account Name:	 roees$@FORTSCALE.DOM Account Domain:	 FORTSCALE.DOM Logon GUID:	 {99448FA4-399E-D675-CD76-9212248B50D8} Service Information: Service Name:	 FS-DC-01$ Service ID:	 FORTSCALE\\FS-DC-01$ Network Information: Client Address:	 ::ffff:192.168.100.141 Client Port:	 58076 Additional Information: Ticket Options:	 0x40810000 Ticket Encryption Type:	0x12 Failure Code:	 0x0 Transited Services:	- This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	null
        		),
        		$ (
		        "4769 Event with empty account name (Should be dropped)",
		    	"2014-06-24T10:16:05.000+03:00|06/24/2014 10:16:05 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4769	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Service Ticket Operations	OpCode=Info	RecordNumber=260633890	Keywords=Audit Failure	Message=A Kerberos service ticket was requested.		Account Information:		Account Name:				Account Domain:				Logon GUID:		{00000000-0000-0000-0000-000000000000}		Service Information:		Service Name:				Service ID:		NULL SID		Network Information:		Client Address:		::ffff:192.168.0.62		Client Port:		64987		Additional Information:		Ticket Options:		0x10002		Ticket Encryption Type:	0xffffffff		Failure Code:		0x20		Transited Services:	-		This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.		This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.		Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	null
        		),
        		$ (
		        "4769 Event With No Account Name and Service Name (Should be dropped)",
		        "2014-03-21T23:24:58.000+02:00|03/21/2014 11:24:58 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4769 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Service Ticket Operations OpCode=Info RecordNumber=249579343 Keywords=Audit Success Message=A Kerberos service ticket was requested. Account Information: Account Domain:	 FORTSCALE.DOM Logon GUID:	 {99448FA4-399E-D675-CD76-9212248B50D8} Service Information: Service ID:	 FORTSCALE\\FS-DC-01$ Network Information: Client Address:	 ::ffff:192.168.100.141 Client Port:	 58076 Additional Information: Ticket Options:	 0x40810000 Ticket Encryption Type:	0x12 Failure Code:	 0x0 Transited Services:	- This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.",
				null
				),
        		$ (
		        "4769 Event With No Account Name (Should be dropped)",
		        "2014-03-21T23:24:58.000+02:00|03/21/2014 11:24:58 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4769 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Service Ticket Operations OpCode=Info RecordNumber=249579343 Keywords=Audit Success Message=A Kerberos service ticket was requested. Account Information: Account Domain:	 FORTSCALE.DOM Logon GUID:	 {99448FA4-399E-D675-CD76-9212248B50D8} Service Information: Service Name:	 FS-DC-01$ Service ID:	 FORTSCALE\\FS-DC-01$ Network Information: Client Address:	 ::ffff:192.168.100.141 Client Port:	 58076 Additional Information: Ticket Options:	 0x40810000 Ticket Encryption Type:	0x12 Failure Code:	 0x0 Transited Services:	- This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.",
				null
				),
        		$ (
		        "4769 Event With No Service Name (Should be dropped)",
		        "2014-03-21T23:24:58.000+02:00|03/21/2014 11:24:58 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4769 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Service Ticket Operations OpCode=Info RecordNumber=249579343 Keywords=Audit Success Message=A Kerberos service ticket was requested. Account Information: Account Name:	 roees@FORTSCALE.DOM Account Domain:	 FORTSCALE.DOM Logon GUID:	 {99448FA4-399E-D675-CD76-9212248B50D8} Service Information: Service ID:	 FORTSCALE\\FS-DC-01$ Network Information: Client Address:	 ::ffff:192.168.100.141 Client Port:	 58076 Additional Information: Ticket Options:	 0x40810000 Ticket Encryption Type:	0x12 Failure Code:	 0x0 Transited Services:	- This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.",
				null
				),
        		$ (
		        "4769 Event With No Client Address (Should be dropped)",
		        "2014-03-21T23:24:58.000+02:00|03/21/2014 11:24:58 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4769 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Service Ticket Operations OpCode=Info RecordNumber=249579343 Keywords=Audit Success Message=A Kerberos service ticket was requested. Account Information: Account Name:	 roees@FORTSCALE.DOM Account Domain:	 FORTSCALE.DOM Logon GUID:	 {99448FA4-399E-D675-CD76-9212248B50D8} Service Information: Service Name:	 FS-DC-01$ Service ID:	 FORTSCALE\\FS-DC-01$ Network Information: Client Port:	 58076 Additional Information: Ticket Options:	 0x40810000 Ticket Encryption Type:	0x12 Failure Code:	 0x0 Transited Services:	- This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.",
				null
				),
				$ (
        		"4769 Event with krbtgt Service Name (Should be dropped)",
		        "2014-06-17T09:00:00.000-04:00|06/17/2014 09:00:00 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4769	EventType=0	Type=Information	ComputerName=TLVXDCP01.Fortscale.com	TaskCategory=Kerberos Service Ticket Operations	OpCode=Info	RecordNumber=97628557	Keywords=Audit Success	Message=A Kerberos service ticket was requested.		Account Information:		Account Name:		yuvals@FORTSCALE.DOM		Account Domain:		FORTSCALE.COM		Logon GUID:		{7160E105-C605-2936-2B43-B3D49C5EED29}		Service Information:		Service Name:		krbtgt		Service ID:		TLV_DOMAIN\\IL-SP-P-DARFAMM		Network Information:		Client Address:		::ffff:172.16.28.178		Client Port:		61285		Additional Information:		Ticket Options:		0x40810008		Ticket Encryption Type:	0x12		Failure Code:		0x0		Transited Services:	-		This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.		This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.		Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	null
        		),
				$ (
        		"4769 Event with krbtgt Service Name and Domain (Should be dropped)",
		        "2014-06-17T09:00:00.000-04:00|06/17/2014 09:00:00 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4769	EventType=0	Type=Information	ComputerName=TLVXDCP01.Fortscale.com	TaskCategory=Kerberos Service Ticket Operations	OpCode=Info	RecordNumber=97628557	Keywords=Audit Success	Message=A Kerberos service ticket was requested.		Account Information:		Account Name:		yuvals@FORTSCALE.DOM		Account Domain:		FORTSCALE.COM		Logon GUID:		{7160E105-C605-2936-2B43-B3D49C5EED29}		Service Information:		Service Name:		krbtgt@FORTSCALE.DOM		Service ID:		TLV_DOMAIN\\IL-SP-P-DARFAMM		Network Information:		Client Address:		::ffff:172.16.28.178		Client Port:		61285		Additional Information:		Ticket Options:		0x40810008		Ticket Encryption Type:	0x12		Failure Code:		0x0		Transited Services:	-		This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.		This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.		Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	null
        		),
        		$(
	    		"4624 event should be droped",
	    		"2014-07-20T15:30:36.000+02:00|07/20/2014 03:30:36 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4624 EventType=0 Type=Information ComputerName=FS-DC-02.Fortscale.dom TaskCategory=Logon OpCode=Info RecordNumber=59222653 Keywords=Audit Success Message=An account was successfully logged on. Subject: 	Security ID:		NULL SID 	Account Name:		- 	Account Domain:		- 	Logon ID:		0x0 Logon Type:			3 New Logon: 	Security ID:		FORTSCALE\\ROEES-PC$ 	Account Name:		ROEES-PC$ 	Account Domain:		FORTSCALE 	Logon ID:		0x2805e1 	Logon GUID:		{2307381A-386D-053B-61E6-544ED755F9A8} Process Information: 	Process ID:		0x0 	Process Name:		-  Network Information: 	Workstation Name: 	Source Network Address:	192.168.0.158 	Source Port:		63514 Detailed Authentication Information: 	Logon Process:		Kerberos 	Authentication Package:	Kerberos 	Transited Services:	- 	Package Name (NTLM only):	- 	Key Length:		0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.  The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network). The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on. The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases. The authentication information fields provide detailed information about this specific logon request. 	- Logon GUID is a unique identifier that can be used to correlate this event with a KDC event. 	- Transited services indicate which intermediate services have participated in this logon request. 	- Package name indicates which sub-protocol was used among the NTLM protocols. 	- Key length indicates the length of the generated session key. This will be 0 if no session key was requested.",
	    		null
	    		),
				$(
				"4769 event with 127.0.0.1 ip should copy the dc name as hostname (FV-4410)",
				"2014-03-21T23:24:58.000+02:00|03/21/2014 11:24:58 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4769 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Service Ticket Operations OpCode=Info RecordNumber=249579343 Keywords=Audit Success Message=A Kerberos service ticket was requested. Account Information: Account Name:	 roees@FORTSCALE.DOM Account Domain:	 FORTSCALE.DOM Logon GUID:	 {99448FA4-399E-D675-CD76-9212248B50D8} Service Information: Service Name:	 FS-DC-01$ Service ID:	 FORTSCALE\\FS-DC-01$ Network Information: Client Address:	 127.0.0.1 Client Port:	 58076 Additional Information: Ticket Options:	 0x40810000 Ticket Encryption Type:	0x12 Failure Code:	 0x0 Transited Services:	- This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.",
				"2014-03-21T23:24:58.000+02:00,2014-03-21 23:24:58,Kerberos Service Ticket Operations,4769,Security,249579343,Microsoft Windows security auditing.,roees@FORTSCALE.DOM,FORTSCALE.DOM,FS-DC-01,FORTSCALE\\FS-DC-01$,127.0.0.1,0x40810000,0x0,,1395444298,Fs-DC-01.Fortscale.dom,false,,,,,,,,,"
				)
        );
 	}


}
