package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class SplunkSecEventsTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvt_splunk.conf";
	private String[] splunkSecEventsOutputFields = new String[] {"timeGeneratedRaw","timeGenerated","categoryString","eventCode","logfile","recordNumber","sourceName","account_name","account_domain","service_name","service_id","client_address","ticket_options","failure_code","source_network_address","timeGeneratedUnixTime"};

	
	@Before
	public void setUp() throws Exception {
		morphlineTester.init(confFile, splunkSecEventsOutputFields);
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
		        "Kerberos Service Ticket Operations|4769|Security|A Kerberos service ticket was requested.		Account Information:		Account Name:		test_account@COMPANY.DOM		Account Domain:		COMPANY.DOM		Logon GUID:		{342AC5A9-A160-3273-50A4-E49FA79E9D5D}		Service Information:		Service Name:		TEST_SERVICE$		Service ID:		COMPANY\\TEST_SERVICE$		Network Information:		Client Address:		::ffff:192.168.100.141		Client Port:		58255		Additional Information:		Ticket Options:		0x40810000		Ticket Encryption Type:	0x12		Failure Code:		0x0		Transited Services:	-		This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.		This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.		Ticket options, encryption types, and failure codes are defined in RFC 4120.|220728460|Microsoft Windows security auditing.|2014-02-08T12:38:17.000+02:00|Cc-DC-01.Company.dom",
				"2014-02-08T12:38:17.000+02:00,2014-02-08 12:38:17,Kerberos Service Ticket Operations,4769,Security,220728460,Microsoft Windows security auditing.,test_account@COMPANY.DOM,COMPANY.DOM,TEST_SERVICE$,COMPANY\\TEST_SERVICE$,192.168.100.141,0x40810000,0x0,,1391863097"
        		),
        		$ (
		        "4769 Event With No Account Name and Service Name (Should be dropped)",
		        "Kerberos Service Ticket Operations|4769|Security|A Kerberos service ticket was requested.		Account Information:		Account Name:				Account Domain:				Logon GUID:		{00000000-0000-0000-0000-000000000000}		Service Information:		Service Name:				Service ID:		NULL SID		Network Information:		Client Address:		::ffff:192.168.100.141		Client Port:		54048		Additional Information:		Ticket Options:		0x2		Ticket Encryption Type:	0xffffffff		Failure Code:		0x20		Transited Services:	-		This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.		This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.		Ticket options, encryption types, and failure codes are defined in RFC 4120.|219832494|Microsoft Windows security auditing.|2014-02-07T12:12:40.000+02:00|Cc-DC-01.Company.dom",
				null
				),
        		$ (
		        "4769 Event With No Account Name (Should be dropped)",
		        "Kerberos Service Ticket Operations|4769|Security|A Kerberos service ticket was requested.		Account Information:		Account Name:				Account Domain:				Logon GUID:		{00000000-0000-0000-0000-000000000000}		Service Information:		Service Name:		TEST_SERVICE$		Service ID:		COMPANY\\TEST_SERVICE$		Network Information:		Client Address:		::ffff:192.168.100.141		Client Port:		54048		Additional Information:		Ticket Options:		0x2		Ticket Encryption Type:	0xffffffff		Failure Code:		0x20		Transited Services:	-		This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.		This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.		Ticket options, encryption types, and failure codes are defined in RFC 4120.|219832494|Microsoft Windows security auditing.|2014-02-07T12:12:40.000+02:00|Cc-DC-01.Company.dom",
				null
				),
        		$ (
		        "4769 Event With No Service Name (Should be dropped)",
		        "Kerberos Service Ticket Operations|4769|Security|A Kerberos service ticket was requested.		Account Information:		Account Name:		test_account@COMPANY.DOM		Account Domain:		COMPANY.DOM		Logon GUID:		{00000000-0000-0000-0000-000000000000}		Service Information:		Service Name:				Service ID:		NULL SID		Network Information:		Client Address:		::ffff:192.168.100.141		Client Port:		54048		Additional Information:		Ticket Options:		0x2		Ticket Encryption Type:	0xffffffff		Failure Code:		0x20		Transited Services:	-		This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.		This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.		Ticket options, encryption types, and failure codes are defined in RFC 4120.|219832494|Microsoft Windows security auditing.|2014-02-07T12:12:40.000+02:00|Cc-DC-01.Company.dom",
				null
				),
        		$ (
		        "4769 Event With No Client Address (Should be dropped)",
		        "Kerberos Service Ticket Operations|4769|Security|A Kerberos service ticket was requested.		Account Information:		Account Name:		test_account@COMPANY.DOM		Account Domain:		COMPANY.DOM		Logon GUID:		{342AC5A9-A160-3273-50A4-E49FA79E9D5D}		Service Information:		Service Name:		TEST_SERVICE$		Service ID:		COMPANY\\TEST_SERVICE$		Network Information:		Client Address:				Client Port:		58255		Additional Information:		Ticket Options:		0x40810000		Ticket Encryption Type:	0x12		Failure Code:		0x0		Transited Services:	-		This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.		This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.		Ticket options, encryption types, and failure codes are defined in RFC 4120.|220728460|Microsoft Windows security auditing.|2014-02-08T12:38:17.000+02:00|Cc-DC-01.Company.dom",
				null
				)
        );
 	}


}
