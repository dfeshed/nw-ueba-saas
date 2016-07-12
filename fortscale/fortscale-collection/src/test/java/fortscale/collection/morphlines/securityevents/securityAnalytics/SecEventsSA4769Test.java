package fortscale.collection.morphlines.securityevents.securityAnalytics;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.TestUtils;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSA4769Test {

	private static ApplicationContext context;

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/securityAnalytics/readSecEvt.conf";
	private String conf4769File = "resources/conf-files/securityevents/securityAnalytics/processSecEvt4769.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

	final static String Mar_17_15_49_21 = "Mar 17 15:49:21";
	static String Mar_17_15_49_21_OUT1;
	static String Mar_17_15_49_21_OUT2;
	static Long Mar_17_15_49_21_LONG;


	static {
		prepareDates();
	}

	private static void prepareDates() {
		TestUtils.init("yyyy MMM dd HH:mm:ss", "UTC");
		Date date = TestUtils.constuctDate(Mar_17_15_49_21);
		Mar_17_15_49_21_OUT1 = TestUtils.getOutputDate(date, "yyyy-MM-dd'T'HH:mm:ss");
		Mar_17_15_49_21_OUT2 = TestUtils.getOutputDate(date, "yyyy-MM-dd HH:mm:ss");
		Mar_17_15_49_21_LONG = TestUtils.getUnixDate(date);
	}
	
	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		StatsService statsService = (StatsService)context.getBean("nullStatsServiceConfig");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.kerberos_logins.table.morphline.fields");
		List<String> splunkSecEventsOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		List<String> splunkSecEventsOutputFieldsExcludingEnrichment = new ArrayList<>();
		for(String field: splunkSecEventsOutputFields){
			if(!field.equals("machine_name")){
				splunkSecEventsOutputFieldsExcludingEnrichment.add(field);
			}
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
		        "Successfull 4769 Event Type 1",
						Mar_17_15_49_21 + " IL-DC2 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.#177#177Account Information:#177Account Name:roman_s@IL.PLAYTECH.CORP#177Account Domain:IL.PLAYTECH.CORP#177Logon GUID:#177{EBB1D035-9AE8-D80F-366E-0BD293955C58}#177#177Service Information:#177Service Name:IL-EXCH$#177Service ID:#177S-1-5-21-2289726844-590661003-2420928919-6123#177#177Network Information:#177Client Address:#177::ffff:192.168.158.167#177Client Port:62952#177#177Additional Information:#177Ticket Options:0x40810000#177Ticket Encryption Type:0x12#177Failure Code:0x0#177Transited Services:-#177#177This event is generated every time access is requested to a#177resource such as a computer or a Windows service.  The#177service name indicates the resource to which access was#177requested.#177#177This event can be correlated with Windows logon events by#177comparing the Logon GUID fields in each event.  The logon#177event occurs on the machine that was accessed, which is#177often a different machine than the domain controller which#177issued the service ticket.#177#177Ticket options, encr ",
						Mar_17_15_49_21_OUT1 + ".000Z," + Mar_17_15_49_21_OUT2 + ",,4769,,,microsoft-windows-security-auditing,roman_s@IL.PLAYTECH.CORP,IL.PLAYTECH.CORP,IL-EXCH,S-1-5-21-2289726844-590661003-2420928919-6123,192.168.158.167,0x40810000,0x0,," + Mar_17_15_49_21_LONG + ",false,,,,,,,,,"
        		),

        		$ (
		        "Successfull 4769 Event Type 2",
		    	"May  1 10:14:55 roee-hd5 %NICWIN-4-Security_4769_Microsoft-Windows-Security-Auditing: Security,rn=668424399 cid=11272 eid=628,Mon Mar 17 13:49:03 2014,4769,Microsoft-Windows-Security-Auditing,,Audit Success,BG-DC1.bg.playtech.corp,Kerberos Service Ticket Operations,,A Kerberos service ticket was requested.  Account Information:  Account Name:  Plamenv@BG.PLAYTECH.CORP  Account Domain:  BG.PLAYTECH.CORP  Logon GUID:  {C3A9E22D-636B-22E1-B056-F86D42CD69AD}  Service Information:  Service Name:  BG-DC1$  Service ID:  S-1-5-21-3421828858-1269048617-336047487-1898  Network Information:  Client Address:  ::ffff:192.168.203.228  Client Port:  64165  Additional Information:  Ticket Options:  0x40800000  Ticket Encryption Type: 0x12  Failure Code:  0x0  Transited Services: -  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	"2014-03-17T13:49:03.000Z,2014-03-17 13:49:03,,4769,Security,668424399,Microsoft-Windows-Security-Auditing,Plamenv@BG.PLAYTECH.CORP,BG.PLAYTECH.CORP,BG-DC1,S-1-5-21-3421828858-1269048617-336047487-1898,192.168.203.228,0x40800000,0x0,,1395064143,false,,,,,,,,,"
        		),

				$ (
						"Successfull 4769 Double Event",
						"%NICWIN-4-Security_4769_Microsoft-Windows-Security-Auditing: Security,rn=1196594807 cid=0x00003801 eid=0x000012a1,Wed Dec 02 08:36:00 2015,4769,Microsoft-Windows-Security-Auditing,None,Failure Audit,BLRKECGDC11.ad.infosys.com,Kerberos Service Ticket Operations,,A Kerberos service ticket was requested.  Account Information:  Account Name:     Account Domain:     Logon GUID:  {00000000-0000-0000-0000-000000000000}   Service Information:  Service Name:     Service ID:  /NULL SID   Network Information:  Client Adcrosoft-Windows-Security-Auditing: Security,rn=131183754 cid=0x00003801 eid=0x000012a1,Wed Dec 02 08:27:57 2015,4769,Microsoft-Windows-Security-Auditing,None,Success Audit,BLRKECGDC12.ad.infosys.com,Kerberos Service Ticket Operations,,A Kerberos service ticket was requested.  Account Information:  Account Name:  Vinay_R@AD.INFOSYS.COM   Account Domain:  AD.INFOSYS.COM   Logon GUID:  {3A72C8D4-177C-E258-5C66-CD7ABAB6645C}   Service Information:  Service Name:  BLRKECIDC05$   Service ID:  ITLINFOSYS/BLRKECIDC05$   Network Information:  Client Address:  ::ffff:10.68.134.15   Client Port:  64331   Additional Information:  Ticket Options:  0x40810000   Ticket Encryption Type: 0x12   Failure Code:  0x0   Transited Services: -   This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encryption types, and failure codes are defined in RFC 4120.",
						"2015-12-02T08:36:00.000Z,2015-12-02 08:36:00,,4769,Security,rn=1196594807 cid=0x00003801 eid=0x000012a1,Wed Dec 02 08:36:00 2015,4769,Microsoft-Windows-Security-Auditing,None,Failure Audit,BLRKECGDC11.ad.infosys.com,Kerberos Service Ticket Operations,,A Kerberos service ticket was requested.  Account Information:  Account Name:     Account Domain:     Logon GUID:  {00000000-0000-0000-0000-000000000000}   Service Information:  Service Name:     Service ID:  /NULL SID   Network Information:  Client Adcrosoft-Windows-Security-Auditing: Security,131183754,,Vinay_R@AD.INFOSYS.COM,AD.INFOSYS.COM,BLRKECIDC05,ITLINFOSYS/BLRKECIDC05$,10.68.134.15,0x40810000,0x0,,1449045360,false,,,,,,,,,"
				),

        		$ (
		        "Successfull 4769 with Empty Account Name Event Type 1 (Should be dropped)",
		    	"Mar 17 15:49:21 IL-DC2 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.#177#177Account Information:#177Account Name:#177Account Domain:IL.PLAYTECH.CORP#177Logon GUID:#177{EBB1D035-9AE8-D80F-366E-0BD293955C58}#177#177Service Information:#177Service Name:IL-EXCH$#177Service ID:#177S-1-5-21-2289726844-590661003-2420928919-6123#177#177Network Information:#177Client Address:#177::ffff:192.168.158.167#177Client Port:62952#177#177Additional Information:#177Ticket Options:0x40810000#177Ticket Encryption Type:0x12#177Failure Code:0x0#177Transited Services:-#177#177This event is generated every time access is requested to a#177resource such as a computer or a Windows service.  The#177service name indicates the resource to which access was#177requested.#177#177This event can be correlated with Windows logon events by#177comparing the Logon GUID fields in each event.  The logon#177event occurs on the machine that was accessed, which is#177often a different machine than the domain controller which#177issued the service ticket.#177#177Ticket options, encr ",
		    	null
        		),

        		$ (
        		"Successfull 4769 with Empty Account Name Event Type 2 (Should be dropped)",
		    	"May  1 10:14:55 roee-hd5 %NICWIN-4-Security_4769_Microsoft-Windows-Security-Auditing: Security,rn=668424399 cid=11272 eid=628,Mon Mar 17 13:49:03 2014,4769,Microsoft-Windows-Security-Auditing,,Audit Success,BG-DC1.bg.playtech.corp,Kerberos Service Ticket Operations,,A Kerberos service ticket was requested.  Account Information:  Account Name:    Account Domain:  BG.PLAYTECH.CORP  Logon GUID:  {C3A9E22D-636B-22E1-B056-F86D42CD69AD}  Service Information:  Service Name:  BG-DC1$  Service ID:  S-1-5-21-3421828858-1269048617-336047487-1898  Network Information:  Client Address:  ::ffff:192.168.203.228  Client Port:  64165  Additional Information:  Ticket Options:  0x40800000  Ticket Encryption Type: 0x12  Failure Code:  0x0  Transited Services: -  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	null
        		),

        		$ (
		        "4769 with krbtgt (should be dropped)",
		    	"May  1 10:14:55 roee-hd5 %NICWIN-4-Security_4769_Microsoft-Windows-Security-Auditing: Security,rn=668424399 cid=11272 eid=628,Mon Mar 17 13:49:03 2014,4769,Microsoft-Windows-Security-Auditing,,Audit Success,BG-DC1.bg.playtech.corp,Kerberos Service Ticket Operations,,A Kerberos service ticket was requested.  Account Information:  Account Name:  Plamenv@BG.PLAYTECH.CORP  Account Domain:  BG.PLAYTECH.CORP  Logon GUID:  {C3A9E22D-636B-22E1-B056-F86D42CD69AD}  Service Information:  Service Name:  krbtgt  Service ID:  S-1-5-21-3421828858-1269048617-336047487-1898  Network Information:  Client Address:  ::ffff:192.168.203.228  Client Port:  64165  Additional Information:  Ticket Options:  0x40800000  Ticket Encryption Type: 0x12  Failure Code:  0x0  Transited Services: -  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	null
        		),
        		$ (
				"4769 with krbtgt full domain name (should be dropped)",
		    	"May  1 10:14:55 roee-hd5 %NICWIN-4-Security_4769_Microsoft-Windows-Security-Auditing: Security,rn=668424399 cid=11272 eid=628,Mon Mar 17 13:49:03 2014,4769,Microsoft-Windows-Security-Auditing,,Audit Success,BG-DC1.bg.playtech.corp,Kerberos Service Ticket Operations,,A Kerberos service ticket was requested.  Account Information:  Account Name:  Plamenv@BG.PLAYTECH.CORP  Account Domain:  BG.PLAYTECH.CORP  Logon GUID:  {C3A9E22D-636B-22E1-B056-F86D42CD69AD}  Service Information:  Service Name:  krbtgt@FORTSCALE.DOM  Service ID:  S-1-5-21-3421828858-1269048617-336047487-1898  Network Information:  Client Address:  ::ffff:192.168.203.228  Client Port:  64165  Additional Information:  Ticket Options:  0x40800000  Ticket Encryption Type: 0x12  Failure Code:  0x0  Transited Services: -  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	null
        		)

        );

 	}


}
