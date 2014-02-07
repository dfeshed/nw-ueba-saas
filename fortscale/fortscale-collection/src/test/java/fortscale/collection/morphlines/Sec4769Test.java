package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitesdk.morphline.api.Record;

@RunWith(JUnitParamsRunner.class)
public class Sec4769Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/processSecEvt4769.conf";
	private String[] sec4769OutputFields = new String[] {"account_name","account_domain","service_name","service_id","client_address","ticket_options","failure_code","source_network_address","client_hostname"};

	
	@Before
	public void setUp() throws Exception {
		morphlineTester.init(confFile, sec4769OutputFields);
	}

	@Test
	public void testSuccessfull4769Event() {
		String testCase = "Successfull 4769 Event";
		Record testRecord = new Record();
		testRecord.put("messageData", "A Kerberos service ticket was requested.          Account Information:            Account Name:           testuser@COMPANY.DOM              Account Domain:         COMPANY.DOM           Logon GUID:             {5DCEB640-D4C8-9E9D-880A-35F529CED35F}         Service Information:             Service Name:           thisismyservice$         Service ID:             COMPANY\thisismyservice$         Network Information:            Client Address:::ffff:192.168.99.98             Client Port:            33684           Additional Information:         Ticket Options:         0x40800000              Ticket Encryption Type: 0x12            Failure Code:           0x0             Transited Services:     -               This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.           This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.               Ticket options, encryption types, and failure codes are defined in RFC 4120.");
		String expectedOutput = "testuser@COMPANY.DOM|COMPANY.DOM|thisismyservice$|COMPANY\thisismyservice$|192.168.99.98|0x40800000|0x0|||";
		morphlineTester.testSingleRecord(testCase, testRecord, expectedOutput);
	}

	
	@Test
	public void test4769WithoutAccountNameAndServiceName() {
		String testCase = "4769 Event With No Account Name and Service Name (Should be dropped)";
		Record testRecord = new Record();
		testRecord.put("messageData", "A Kerberos service ticket was requested.          Account Information:            Account Name:                           Account Domain:                         Logon GUID:             {00000000-0000-0000-0000-000000000000}          Service Information:   Service Name:                            Service ID:             NULL SID                Network Information:            Client Address:         ::ffff:192.168.0.110   Client Port:             53570           Additional Information:         Ticket Options:         0x2             Ticket Encryption Type: 0xffffffff              Failure Code:           0x20            Transited Services:     -               This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.           This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.               Ticket options, encryption types, and failure codes are defined in RFC 4120.");
		String expectedOutput = null;
		morphlineTester.testSingleRecord(testCase, testRecord, expectedOutput);
	}
	
	
	@Test
	public void test4769WithoutAccountName() {
		String testCase = "4769 Event With No Account Name (Should be dropped)";
		Record testRecord = new Record();
		testRecord.put("messageData", "A Kerberos service ticket was requested.          Account Information:            Account Name:                           Account Domain:                         Logon GUID:             {00000000-0000-0000-0000-000000000000}          Service Information:   Service Name:           THISISMYSERVICENAME$         Service ID:             NULL SID                Network Information:            Client Address:         ::ffff:192.168.0.110   Client Port:             53570           Additional Information:         Ticket Options:         0x2             Ticket Encryption Type: 0xffffffff              Failure Code:           0x20            Transited Services:     -               This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.           This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.               Ticket options, encryption types, and failure codes are defined in RFC 4120.");
		String expectedOutput = null;
		morphlineTester.testSingleRecord(testCase, testRecord, expectedOutput);
	}
	
	
	@Test
	public void test4769WithoutServiceName() {
		String testCase = "4769 Event With No Service Name (Should be dropped)";
		Record testRecord = new Record();
		testRecord.put("messageData", "A Kerberos service ticket was requested.          Account Information:            Account Name:           testuser@COMPANY.DOM              Account Domain:                         Logon GUID:             {00000000-0000-0000-0000-000000000000}          Service Information:   Service Name:                            Service ID:             NULL SID                Network Information:            Client Address:         ::ffff:192.168.0.110   Client Port:             53570           Additional Information:         Ticket Options:         0x2             Ticket Encryption Type: 0xffffffff              Failure Code:           0x20            Transited Services:     -               This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.           This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.               Ticket options, encryption types, and failure codes are defined in RFC 4120.|218827049|Microsoft Windows security auditing.");
		String expectedOutput = null;
		morphlineTester.testSingleRecord(testCase, testRecord, expectedOutput);
	}
	
	
	@Test
	public void test4769WithoutClientAddress() {
		String testCase = "4769 Event With No Client Address (Should be dropped)";
		Record testRecord = new Record();
		testRecord.put("messageData", "Kerberos Service Ticket Operations|4769|Security|A Kerberos service ticket was requested.          Account Information:            Account Name:           testuser@COMPANY.DOM              Account Domain:                         Logon GUID:             {00000000-0000-0000-0000-000000000000}          Service Information:   Service Name:           THISISMYSERVICENAME$         Service ID:             NULL SID                Network Information:            Client Address:            Client Port:             53570           Additional Information:         Ticket Options:         0x2             Ticket Encryption Type: 0xffffffff              Failure Code:           0x20            Transited Services:     -               This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.           This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.               Ticket options, encryption types, and failure codes are defined in RFC 4120.|218827049|Microsoft Windows security auditing.|2014-02-05T11:03:19.000+02:00|CC-DC-01.Company.dom");
		String expectedOutput = null;
		morphlineTester.testSingleRecord(testCase, testRecord, expectedOutput);
	}

}
