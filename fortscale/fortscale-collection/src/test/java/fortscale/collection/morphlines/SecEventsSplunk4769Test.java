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
public class SecEventsSplunk4769Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvt_splunk.conf";
	private String conf4769File = "resources/conf-files/processSecEvt4769.conf";

	
	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.security.events.4769.table.morphline.fields");
		List<String> splunkSecEventsOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		List<String> splunkSecEventsOutputFieldsExcludingEnrichment = new ArrayList<>();
		for(String field: splunkSecEventsOutputFields){
			if(!field.equals("machine_name")){
				splunkSecEventsOutputFieldsExcludingEnrichment.add(field);
			}
		}
		morphlineTester.init(new String[] { confFile, conf4769File }, splunkSecEventsOutputFieldsExcludingEnrichment);
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
		    	"2014-03-21T23:24:58.000+02:00,2014-03-21 23:24:58,Kerberos Service Ticket Operations,4769,Security,249579343,Microsoft Windows security auditing.,roees@FORTSCALE.DOM,FORTSCALE.DOM,FS-DC-01,FORTSCALE\\FS-DC-01$,192.168.100.141,0x40810000,0x0,,1395437098,false,,,,,false,false,false"
        		),
        		$ (
        		"Successfull 4769 Event with Service Name that is without $",
		        "2014-06-17T09:00:00.000-04:00|06/17/2014 09:00:00 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4769	EventType=0	Type=Information	ComputerName=TLVXDCP01.Fortscale.com	TaskCategory=Kerberos Service Ticket Operations	OpCode=Info	RecordNumber=97628557	Keywords=Audit Success	Message=A Kerberos service ticket was requested.		Account Information:		Account Name:		IL-SP-P-DARFAMM@FORTSCALE.COM		Account Domain:		FORTSCALE.COM		Logon GUID:		{7160E105-C605-2936-2B43-B3D49C5EED29}		Service Information:		Service Name:		IL-SP-P-DARFAMM		Service ID:		TLV_DOMAIN\\IL-SP-P-DARFAMM		Network Information:		Client Address:		::ffff:172.16.28.178		Client Port:		61285		Additional Information:		Ticket Options:		0x40810008		Ticket Encryption Type:	0x12		Failure Code:		0x0		Transited Services:	-		This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.		This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.		Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	"2014-06-17T09:00:00.000-04:00,2014-06-17 09:00:00,Kerberos Service Ticket Operations,4769,Security,97628557,Microsoft Windows security auditing.,IL-SP-P-DARFAMM@FORTSCALE.COM,FORTSCALE.COM,IL-SP-P-DARFAMM,TLV_DOMAIN\\IL-SP-P-DARFAMM,172.16.28.178,0x40810008,0x0,,1402984800,false,,,,"
        		),
        		$ (
		        "4769 Event with computer as account name (Should be dropped)", 
		    	"2014-03-21T23:24:58.000+02:00|03/21/2014 11:24:58 PM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4769 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Service Ticket Operations OpCode=Info RecordNumber=249579343 Keywords=Audit Success Message=A Kerberos service ticket was requested. Account Information: Account Name:	 roees$@FORTSCALE.DOM Account Domain:	 FORTSCALE.DOM Logon GUID:	 {99448FA4-399E-D675-CD76-9212248B50D8} Service Information: Service Name:	 FS-DC-01$ Service ID:	 FORTSCALE\\FS-DC-01$ Network Information: Client Address:	 ::ffff:192.168.100.141 Client Port:	 58076 Additional Information: Ticket Options:	 0x40810000 Ticket Encryption Type:	0x12 Failure Code:	 0x0 Transited Services:	- This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.",
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
				)
        );
 	}


}
