package fortscale.collection.morphlines;

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
public class SecEventsNitro4769Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvt_nitro.conf";
	private String conf4769File = "resources/conf-files/processSecEvt4769_nitro.conf";
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
		        "Computer 4769 Event - Drop event",
		    	"144117387552096256,130784853,ELM,1443657754,,,10.8.48.101||Security||<BookmarkList>%0D   <Bookmark Channel='Security' RecordId='213561524' IsCurrent='true'/>%0D </BookmarkList>||Microsoft-Windows-Security-Auditing||4769||262||1443657093||0||SNCH2DCS01.sicpa-net.ads||||||11||JBarthod$@SICPA-NET.ADS||SICPA-NET.ADS||CH2PRT25V$||S-1-5-21-1324571244-530250876-991709287-14052||0x40810000||0x12||::ffff:10.8.12.19||51832||0x0||23710201-ec01-7100-926d-d6628645d62a||-||A Kerberos service ticket was requested.%0D %0D Account Information:%0D %09Account Name:%09%09%251%0D %09Account Domain:%09%09%252%0D %09Logon GUID:%09%09%2510%0D %0D Service Information:%0D %09Service Name:%09%09%253%0D %09Service ID:%09%09%254%0D %0D Network Information:%0D %09Client Address:%09%09%257%0D %09Client Port:%09%09%258%0D %0D Additional Information:%0D %09Ticket Options:%09%09%255%0D %09Ticket Encryption Type:%09%256%0D %09Failure Code:%09%09%259%0D %09Transited Services:%09%2511%0D %0D This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.%0D %0D This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.%0D %0D Ticket options, encryption types, and failure codes are defined in RFC 4120.",
		    	null
        		),
				$ (
				"Successfull 4769 Event",
				"144117387552096256,121169057,ELM,1441065972,,,10.8.48.101||Security||<BookmarkList>%0D   <Bookmark Channel='Security' RecordId='203987400' IsCurrent='true'/>%0D </BookmarkList>||Microsoft-Windows-Security-Auditing||4769||262||1441065330||0||SNCH2DCS01.sicpa-net.ads||||||11||SNCH2SQL8078V@SICPA-NET.ADS||SICPA-NET.ADS||JHarperADM||S-1-5-21-1324571244-530250876-991709287-3642||0x40810008||0x12||::ffff:10.8.48.78||49219||0x0||dd673d4c-1fa0-68f3-f616-ad6c2124a883||-||A Kerberos service ticket was requested.%0D %0D Account Information:%0D %09Account Name:%09%09%251%0D %09Account Domain:%09%09%252%0D %09Logon GUID:%09%09%2510%0D %0D Service Information:%0D %09Service Name:%09%09%253%0D %09Service ID:%09%09%254%0D %0D Network Information:%0D %09Client Address:%09%09%257%0D %09Client Port:%09%09%258%0D %0D Additional Information:%0D %09Ticket Options:%09%09%255%0D %09Ticket Encryption Type:%09%256%0D %09Failure Code:%09%09%259%0D %09Transited Services:%09%2511%0D %0D This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.%0D %0D This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.%0D %0D Ticket options, encryption types, and failure codes are defined in RFC 4120.",
				"1441065330,2015-08-31 23:55:30,Kerberos Service Ticket Operations,4769,Security,0,Microsoft Windows security auditing.,JHarperADM,SICPA-NET.ADS,SNCH2SQL8078V@SICPA-NET.ADS,S-1-5-21-1324571244-530250876-991709287-3642,10.8.48.78,0x40810008,0x0,,1441065330,,false,,,,,,,,"
				)
        );
 	}


}
