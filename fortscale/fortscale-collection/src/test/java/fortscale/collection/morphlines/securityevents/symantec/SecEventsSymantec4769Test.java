package fortscale.collection.morphlines.securityevents.symantec;

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
public class SecEventsSymantec4769Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/symantec/readSecEvt.conf";
	private String conf4769File = "resources/conf-files/securityevents/symantec/processSecEvt4769.conf";
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
						"4769 event",
						"<Event xmlns=\"http://schemas.microsoft.com/win/2004/08/events/event\"><System><Provider Guid=\"{GUID}\" Name=\"Microsoft-Windows-Security-Auditing\"/><EventID>4769</EventID><Version>0</Version><Level>0</Level><Task>14337</Task><Opcode>0</Opcode><Keywords>0x8020000000000000</Keywords><TimeCreated SystemTime=\"2015-10-31T22:05:21.075681900Z\"/><EventRecordID>2503683730</EventRecordID><Correlation/><Execution ProcessID=\"792\" ThreadID=\"11352\"/><Channel>Security</Channel><Computer>computername</Computer><Security/></System><EventData><Data Name=\"TargetUserName\">username@Domain</Data><Data Name=\"TargetDomainName\">Domain</Data><Data Name=\"ServiceName\">ServiceName</Data><Data Name=\"ServiceSid\">S-1-5-21-1275210071-1606980848-682003330-25098</Data><Data Name=\"TicketOptions\">0x40810000</Data><Data Name=\"TicketEncryptionType\">0x12</Data><Data Name=\"IpAddress\">::ffff:2.2.2.2</Data><Data Name=\"IpPort\">17031</Data><Data Name=\"Status\">0x0</Data><Data Name=\"LogonGuid\">{GUID}</Data><Data Name=\"TransmittedServices\">-</Data></EventData><RenderingInfo Culture=\"en-US\"><Message>A Kerberos service ticket was requested.  Account Information:         Account Name:           username@Domain         Account Domain:         Domain  Logon GUID:             {GUID}  Service Information:    Service Name:           ServiceName     Service ID:             S-1-5-21-1275210071-1606980848-682003330-25098  Network Information:    Client Address:         ::ffff:2.2.2.2  Client Port:            17031  Additional Information:  Ticket Options:         0x40810000      Ticket Encryption Type: 0x12    Failure Code:           0x0     Transited Services:     -  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encryption types, and failure codes are defined in RFC 4120.</Message><Level>Information</Level><Task>Kerberos Service Ticket Operations</Task><Opcode>Info</Opcode><Channel>Security</Channel><Provider>Microsoft Windows security auditing.</Provider><Keywords><Keyword>Audit Success</Keyword></Keywords></RenderingInfo></Event>\" (service map: <eventmap version=\"2\"><field name=\"TimeOffset\"></field><field name=\"reporting_sensor\">hostname</field><field name=\"proxy_machine\">hostname</field></eventmap>)",
						"2015-10-31T22:05:21.075681900Z,2015-11-01 19:06:42,Kerberos Service Ticket Operations,4769,Security,1,Microsoft Windows security auditing.,username@Domain,Domain,ServiceName,S-1-5-21-1275210071-1606980848-682003330-25098,2.2.2.2,0x40810000,0x0,,1446404802,,false,,,,,,,,,"
				)
		);
	}


}
