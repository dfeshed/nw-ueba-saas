package fortscale.collection.morphlines.securityevents.syslog;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import fortscale.collection.morphlines.MorphlinesTester;
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
public class SecEventsSyslog4769Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/syslog/readSecEvt.conf";
	private String conf4769File = "resources/conf-files/securityevents/syslog/processSecEvt4769.conf";
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
						"4769 event with no space after service ID",
						"May 28 14:09:40 il-dc1 microsoft-windows-security-auditing[failure] 4769 A Kerberos service ticket was requested.  Account Information: Account Name:ariel_l@IL.PLAYTECH.CORP Account Domain:IL.PLAYTECH.CORP Logon GUID: {00000000-0000-0000-0000-000000000000}  Service Information: Service Name:host/il-printserv1 Service ID:S-1-0-0  Network Information: Client Address: ::ffff:192.168.158.225 Client Port:49279  Additional Information: Ticket Options:0x40810000 Ticket Encryption Type:0xffffffff Failure Code:0x12 Transited Services:-  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encryption types, and fail",
						"May 28 14:09:40,2016-05-28 14:09:40,Kerberos Service Ticket Operations,4769,Security,1,Microsoft Windows security auditing.,ariel_l@IL.PLAYTECH.CORP,IL.PLAYTECH.CORP,host/il-printserv1,S-1-0-0,192.168.158.225,0x40810000,0x12,,1464444580,,false,,,,,,,,,"
				),
				$ (
						"Successfull 4769 Event",
						"May 31 20:18:44 IL-DC2 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.  Account Information: Account Name:asher_y@IL.PLAYTECH.CORP Account Domain:IL.PLAYTECH.CORP Logon GUID: {DD01473F-283C-B961-06B0-D3A4834030F2}  Service Information: Service Name:IL-CAS1$ Service ID: S-1-5-21-2289726844-590661003-2420928919-8351  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47358  Additional Information: Ticket Options:0x40810000 Ticket Encryption Type:0x12 Failure Code:0x0 Transited Services:-  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encrypt",
						"May 31 20:18:44,2015-05-31 20:18:44,Kerberos Service Ticket Operations,4769,Security,1,Microsoft Windows security auditing.,asher_y@IL.PLAYTECH.CORP,IL.PLAYTECH.CORP,IL-CAS1,S-1-5-21-2289726844-590661003-2420928919-8351,192.168.7.34,0x40810000,0x0,,1433103524,,false,,,,,,,,,"
				),
				$ (
						"Successfull 4769 Event with Service Name that is without $",
						"May 31 20:18:44 IL-DC2 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.  Account Information: Account Name:asher_y@IL.PLAYTECH.CORP Account Domain:IL.PLAYTECH.CORP Logon GUID: {DD01473F-283C-B961-06B0-D3A4834030F2}  Service Information: Service Name:IL-CAS1 Service ID: S-1-5-21-2289726844-590661003-2420928919-8351  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47358  Additional Information: Ticket Options:0x40810000 Ticket Encryption Type:0x12 Failure Code:0x0 Transited Services:-  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encrypt",
						"May 31 20:18:44,2015-05-31 20:18:44,Kerberos Service Ticket Operations,4769,Security,1,Microsoft Windows security auditing.,asher_y@IL.PLAYTECH.CORP,IL.PLAYTECH.CORP,IL-CAS1,S-1-5-21-2289726844-590661003-2420928919-8351,192.168.7.34,0x40810000,0x0,,1433103524,,false,,,,,,,,,"
				),
				$ (
						"4769 Event with computer as account name (Should be dropped)",
						"May 31 20:18:43 il-dc1 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.  Account Information: Account Name: IL-TMUROTDB$@IL.PLAYTECH.CORP Account Domain:IL.PLAYTECH.CORP Logon GUID: {6441EB8D-E5BE-3249-6E2B-D69963CF5FDB}  Service Information: Service Name:IL-DC2$ Service ID: S-1-5-21-2289726844-590661003-2420928919-3984  Network Information: Client Address: ::ffff:192.168.7.87 Client Port:54180  Additional Information: Ticket Options:0x40810000 Ticket Encryption Type:0x12 Failure Code:0x0 Transited Services:-  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, en",
						null
				),
				$ (
						"4769 Event with empty account name (Should be dropped)",
						"May 31 20:18:44 IL-DC2 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.  Account Information: Account Name: Account Domain:IL.PLAYTECH.CORP Logon GUID: {DD01473F-283C-B961-06B0-D3A4834030F2}  Service Information: Service Name:IL-CAS1$ Service ID: S-1-5-21-2289726844-590661003-2420928919-8351  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47358  Additional Information: Ticket Options:0x40810000 Ticket Encryption Type:0x12 Failure Code:0x0 Transited Services:-  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encrypt",
						null
				),
				$ (
						"4769 Event With No Account Name and Service Name (Should be dropped)",
						"May 31 20:18:44 IL-DC2 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.  Account Information: Account Domain:IL.PLAYTECH.CORP Logon GUID: {DD01473F-283C-B961-06B0-D3A4834030F2}  Service Information: Service ID: S-1-5-21-2289726844-590661003-2420928919-8351  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47358  Additional Information: Ticket Options:0x40810000 Ticket Encryption Type:0x12 Failure Code:0x0 Transited Services:-  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encrypt",
						null
				),
				$ (		 "4769 Event With No Account Name (Should be dropped)",
						"May 31 20:18:44 IL-DC2 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.  Account Information: Account Domain:IL.PLAYTECH.CORP Logon GUID: {DD01473F-283C-B961-06B0-D3A4834030F2}  Service Information: Service Name:IL-CAS1$ Service ID: S-1-5-21-2289726844-590661003-2420928919-8351  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47358  Additional Information: Ticket Options:0x40810000 Ticket Encryption Type:0x12 Failure Code:0x0 Transited Services:-  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encrypt",
						null
				),
				$ ( "4769 Event With No Service Name (Should be dropped)",
						"May 31 20:18:44 IL-DC2 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.  Account Information: Account Name:asher_y@IL.PLAYTECH.CORP Account Domain:IL.PLAYTECH.CORP Logon GUID: {DD01473F-283C-B961-06B0-D3A4834030F2}  Service Information: Service ID: S-1-5-21-2289726844-590661003-2420928919-8351  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47358  Additional Information: Ticket Options:0x40810000 Ticket Encryption Type:0x12 Failure Code:0x0 Transited Services:-  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encrypt",
						null
				),
				$ (
						"4769 Event With No Client Address (Should be dropped)",
						"May 31 20:18:44 IL-DC2 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.  Account Information: Account Name:asher_y@IL.PLAYTECH.CORP Account Domain:IL.PLAYTECH.CORP Logon GUID: {DD01473F-283C-B961-06B0-D3A4834030F2}  Service Information: Service Name:IL-CAS1$ Service ID: S-1-5-21-2289726844-590661003-2420928919-8351  Network Information: Client Port:47358  Additional Information: Ticket Options:0x40810000 Ticket Encryption Type:0x12 Failure Code:0x0 Transited Services:-  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encrypt",
						null
				),
				$ (
						"4769 Event with krbtgt Service Name (Should be dropped)",
						"May 31 20:18:45 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:amos_s Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-10374  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47362  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						null
				),
				$ (
						"4769 Event with krbtgt Service Name and Domain (Should be dropped)",
						"May 31 20:18:45 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:amos_s Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-10374  Service Information: Service Name:krbtgt@fortscale.com Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47362  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						null
				),
				$(
						"4624 event should be droped",
						"May 31 20:18:42 il-dc1 microsoft-windows-security-auditing[success] 4624 An account was successfully logged on.  Subject: Security ID:S-1-0-0 Account Name:- Account Domain:- Logon ID:0x0  Logon Type:3  Impersonation Level:%1833  New Logon: Security ID: S-1-5-21-2289726844-590661003-2420928919-1726 Account Name:besadmin Account Domain:IL Logon ID:0x21be6b59 Logon GUID: {58E05409-EE3E-2EBA-DFDC-8455FB950FE1}  Process Information: Process ID:0x0 Process Name:-  Network Information: Workstation Name: Source Network Address:192.168.7.100 Source Port:63658  Detailed Authentication Information: Logon Process:Kerberos Authentication Package:Kerberos Transited Services:- Package Name (NTLM only):- Key Length:0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Service",
						null
				)
		);
	}


}
