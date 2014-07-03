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
public class SecEventsSA4769Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvt_securityAnalytics.conf";
	private String conf4769File = "resources/conf-files/processSecEvtSA4769.conf";

	
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
		        "Successfull 4769 Event Type 1", 
		    	"Mar 17 15:49:21 IL-DC2 microsoft-windows-security-auditing[success] 4769 A Kerberos service ticket was requested.#177#177Account Information:#177Account Name:roman_s@IL.PLAYTECH.CORP#177Account Domain:IL.PLAYTECH.CORP#177Logon GUID:#177{EBB1D035-9AE8-D80F-366E-0BD293955C58}#177#177Service Information:#177Service Name:IL-EXCH$#177Service ID:#177S-1-5-21-2289726844-590661003-2420928919-6123#177#177Network Information:#177Client Address:#177::ffff:192.168.158.167#177Client Port:62952#177#177Additional Information:#177Ticket Options:0x40810000#177Ticket Encryption Type:0x12#177Failure Code:0x0#177Transited Services:-#177#177This event is generated every time access is requested to a#177resource such as a computer or a Windows service.  The#177service name indicates the resource to which access was#177requested.#177#177This event can be correlated with Windows logon events by#177comparing the Logon GUID fields in each event.  The logon#177event occurs on the machine that was accessed, which is#177often a different machine than the domain controller which#177issued the service ticket.#177#177Ticket options, encr ",
		    	"2014-03-17T15:49:21.000+02:00,2014-03-17 15:49:21,,4769,,,microsoft-windows-security-auditing,roman_s@IL.PLAYTECH.CORP,IL.PLAYTECH.CORP,IL-EXCH,S-1-5-21-2289726844-590661003-2420928919-6123,192.168.158.167,0x40810000,0x0,,1395064161,false,,,,,false,false,false,false"
        		),

        		$ (
		        "Successfull 4769 Event Type 2", 
		    	"May  1 10:14:55 roee-hd5 %NICWIN-4-Security_4769_Microsoft-Windows-Security-Auditing: Security,rn=668424399 cid=11272 eid=628,Mon Mar 17 13:49:03 2014,4769,Microsoft-Windows-Security-Auditing,,Audit Success,BG-DC1.bg.playtech.corp,Kerberos Service Ticket Operations,,A Kerberos service ticket was requested.  Account Information:  Account Name:  Plamenv@BG.PLAYTECH.CORP  Account Domain:  BG.PLAYTECH.CORP  Logon GUID:  {C3A9E22D-636B-22E1-B056-F86D42CD69AD}  Service Information:  Service Name:  BG-DC1$  Service ID:  S-1-5-21-3421828858-1269048617-336047487-1898  Network Information:  Client Address:  ::ffff:192.168.203.228  Client Port:  64165  Additional Information:  Ticket Options:  0x40800000  Ticket Encryption Type: 0x12  Failure Code:  0x0  Transited Services: -  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	"2014-03-17T13:49:03.000+02:00,2014-03-17 13:49:03,,4769,Security,668424399,Microsoft-Windows-Security-Auditing,Plamenv@BG.PLAYTECH.CORP,BG.PLAYTECH.CORP,BG-DC1,S-1-5-21-3421828858-1269048617-336047487-1898,192.168.203.228,0x40800000,0x0,,1395056943,false,,,,,false,false,false,false"
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
