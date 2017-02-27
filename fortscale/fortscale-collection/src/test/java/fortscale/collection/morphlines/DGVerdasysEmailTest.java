package fortscale.collection.morphlines;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by idanp on 10/5/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context-test-light.xml"})
public class DGVerdasysEmailTest {


	private MorphlinesTester morphlineTester = new MorphlinesTester();


	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass() {

	}

	@Before
	public void setUp() throws Exception {


		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-collection-test.properties");
		String kafkaMessageFields = propertiesResolver.getProperty("kafka.verdasys.message.record.fields");

		List<String> sshMessageOutputFields = ImpalaParser.getTableFieldNames(kafkaMessageFields);
		String confFile = "resources/conf-files/parseDGMail.conf";
		String confEnrichmentFile = "resources/conf-files/enrichment/readDlpMail_enrich.conf";
		morphlineTester.init(new String[]{confFile, confEnrichmentFile}, sshMessageOutputFields);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}


	@Test
	public void test_Outbound_true() {


		String testCase = "Test Outbound is true";
		String inputLine = "2016.04.02,4/2/2016 17:24,4/2/2016  9:24:46 PM,outlook.exe,verdasys\\kdickie-e7450,Windows,,,,,,Kevin,Dickie,A5483BCB-CE71-1F4A-8FC0-B0564EE4EFE0,verdasys\\kdickie,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54,Virus Total: 0 / 57 scans positive.,,,,,outlook,kdickie@digitalguardian.com,RE: Status Update,,9F81A861-7567-1034-4F46-806E2FA73497,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,Outbound,Send Mail,,0,,FALSE,FALSE,FALSE,TRUE,FALSE,TRUE,1,FALSE,0,FALSE,FALSE,FALSE,,,,,,,,,,,,,Not Blocked,0,0,,,,,6AE86C68-E71A-4655-9B38-798E88103360,0,FALSE,,test@gmail.com,,,,,,,,BLANK_EXTENSION,,FALSE,FALSE,FALSE,0,0,FALSE,FALSE,FALSE,Unknown,,None,,kdickie@digitalguardian.com,,,,,";
		String expectedOutput = "2016-04-02 21:24:46,1459632286,kdickie,,,kdickie-e7450,,,,,,,,,,A5483BCB-CE71-1F4A-8FC0-B0564EE4EFE0,,outlook.exe,0,0,,None,,,,,0,outlook,,,,test@gmail.com,gmail.com,,kdickie@digitalguardian.com,RE: Status Update,,Send Mail,,,9F81A861-7567-1034-4F46-806E2FA73497,,,,,true,FALSE,,,,,,0,,,,Unknown,,,,,false,false,false,false,false,false,Kevin Dickie,verdasys,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_Outbound_false() {


		String testCase = "Test Outbound is false";
		String inputLine = "2016.04.02,4/2/2016 17:24,4/2/2016  9:24:46 PM,outlook.exe,verdasys\\kdickie-e7450,Windows,,,,,,Kevin,Dickie,A5483BCB-CE71-1F4A-8FC0-B0564EE4EFE0,verdasys\\kdickie,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54,Virus Total: 0 / 57 scans positive.,,,,,outlook,kdickie@digitalguardian.com,RE: Status Update,,9F81A861-7567-1034-4F46-806E2FA73497,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,Outbound,Send Mail,,0,,FALSE,FALSE,FALSE,TRUE,FALSE,TRUE,1,FALSE,0,FALSE,FALSE,FALSE,,,,,,,,,,,,,Not Blocked,0,0,,,,,6AE86C68-E71A-4655-9B38-798E88103360,0,FALSE,,test@digitalguardian.com,,,,,,,,BLANK_EXTENSION,,FALSE,FALSE,FALSE,0,0,FALSE,FALSE,FALSE,Unknown,,None,,kdickie@digitalguardian.com,,,,,";
		String expectedOutput = "2016-04-02 21:24:46,1459632286,kdickie,,,kdickie-e7450,,,,,,,,,,A5483BCB-CE71-1F4A-8FC0-B0564EE4EFE0,,outlook.exe,0,0,,None,,,,,0,outlook,,,,test@digitalguardian.com,digitalguardian.com,,kdickie@digitalguardian.com,RE: Status Update,,Send Mail,,,9F81A861-7567-1034-4F46-806E2FA73497,,,,,false,FALSE,,,,,,0,,,,Unknown,,,,,false,false,false,false,false,false,Kevin Dickie,verdasys,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_is_attachment_mail_event_field_in_case_of_some_value_on_dest_file() {


		String testCase = "Test is_attachment_mail_event empty destination file mark false";
		String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,regedit.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,0,,test.jpeg,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,test@mail.com,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
		String expectedOutput = "2016-04-03 05:15:16,1459660516,sgurman,,,sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,regedit.exe,0,0,,None,,test.jpeg,,,0,outlook,,,,test@mail.com,mail.com,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,,,true,False,,,,,,0,,,,Unknown,,,,,false,false,true,true,false,false,Susanne Gurman,verdasys,etl";
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}


	/*
    @Test
    public void test_replace_account_name_with_machine_name() {

        String testCase = "Test file type creation";
        String inputLine = "2016.04.04,4/4/2016 9:31:15 AM,4/4/2016 1:31:15 PM,trustevaluationagent,testMachine@doamin.com,Mac,,,,,,,,1BBB8272-F253-467A-8141-9F35910FDD9D,NT AUTHORITY\\NETWORK SERVICE,,,,,,,Scanned,2/13/2015 6:26:34 PM,Virus Total: 0 / 55 scans positive.,,,,,UNKNOWN_HOST,,,,F19FC0CC-47C4-6545-8151-8BE641A93428,,,2c11f21a29e2f7f7346b2d62afa299af,1af2112c-e229-f7f7-346b-2d62afa299af,Inbound,Send Mail,,0,,False,False,False,False,False,True,0,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,50744,\\private\\var\\folders\\zz\\zyxvpxvq6csfxvn_n00000t000006h\\C\\mds\\,mdsDirectory.db_,,db_,F19FC0CD-47C4-6545-8151-8BE641A93428,50744,False,,,,,mdsDirectory.db,,\\private\\var\\db\\mds\\system\\,mdsDirectory.db,,db,,False,False,False,0,0,False,False,False,Fixed,,Fixed,,,,,,,\n";
        String expectedOutput = "2016-04-04 13:31:15,1459776675,Device\\testMachine@doamin.com,,,testMachine@doamin.com,,,,,,,,,,1BBB8272-F253-467A-8141-9F35910FDD9D,,trustevaluationagent,0,50744,,Fixed,\\private\\var\\folders\\zz\\zyxvpxvq6csfxvn_n00000t000006h\\C\\mds\\,mdsDirectory.db_,.db_,.db,50744,,,,,,,,,,,Send Mail,F19FC0CD-47C4-6545-8151-8BE641A93428,,F19FC0CC-47C4-6545-8151-8BE641A93428,,False,,False,false,false,,,,,mdsDirectory.db,0,,\\private\\var\\db\\mds\\system\\,,Fixed,mdsDirectory.db,,False,false,0,0,0,false,verdasys,etl";

        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }


	@Test
	public void test_file_type_creation() {


		String testCase = "Test file type creation";
		String inputLine = "2016.04.04,4/4/2016 9:31:15 AM,4/4/2016 1:31:15 PM,trustevaluationagent,(none)\\Verdasys’s Mac,Mac,,,,,,,,1BBB8272-F253-467A-8141-9F35910FDD9D,Verdasys’s Mac\\_trustevaluationagent,,,,,,,Scanned,2/13/2015 6:26:34 PM,Virus Total: 0 / 55 scans positive.,,,,,UNKNOWN_HOST,,,,F19FC0CC-47C4-6545-8151-8BE641A93428,,,2c11f21a29e2f7f7346b2d62afa299af,1af2112c-e229-f7f7-346b-2d62afa299af,Inbound,File Copy,,0,,False,False,False,False,False,True,0,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,50744,\\private\\var\\folders\\zz\\zyxvpxvq6csfxvn_n00000t000006h\\C\\mds\\,mdsDirectory.db_,,db_,F19FC0CD-47C4-6545-8151-8BE641A93428,50744,False,,,,,mdsDirectory.db,,\\private\\var\\db\\mds\\system\\,mdsDirectory.db,,db,,False,False,False,0,0,False,False,False,Fixed,,Fixed,,,,,,,\n";
		String expectedOutput = "2016-04-04 13:31:15,1459776675,Verdasys’s Mac\\_trustevaluationagent,,,(none)\\Verdasys’s Mac,(none)\\Verdasys’s Mac,,,,,,,,,1BBB8272-F253-467A-8141-9F35910FDD9D,,trustevaluationagent,0,50744,,Fixed,\\private\\var\\folders\\zz\\zyxvpxvq6csfxvn_n00000t000006h\\C\\mds\\,mdsDirectory.db_,.db_,.db,50744,,,,,,,,,,,File Copy,F19FC0CD-47C4-6545-8151-8BE641A93428,,F19FC0CC-47C4-6545-8151-8BE641A93428,,False,,False,false,false,,,,,mdsDirectory.db,0,,\\private\\var\\db\\mds\\system\\,,Fixed,mdsDirectory.db,,False,false,0,0,0,verdasys,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}
	*/

    @Test
    public void test_rdp_mark_true() {


        String testCase = "Test rdp mark true";
        String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,mstsc.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,0,,,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,test@mail.com,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
        String expectedOutput = "2016-04-03 05:15:16,1459660516,sgurman,,,sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,mstsc.exe,0,0,,None,,,,,0,outlook,,,,test@mail.com,mail.com,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,,,true,False,,,,,,0,,,,Unknown,,,,,true,false,false,false,false,false,Susanne Gurman,verdasys,etl";

        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_rdp_mark_false() {


        String testCase = "Test rdp mark false";
        String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,msstsc.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,0,,,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,test@mail.com,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
        String expectedOutput = "2016-04-03 05:15:16,1459660516,sgurman,,,sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,msstsc.exe,0,0,,None,,,,,0,outlook,,,,test@mail.com,mail.com,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,,,true,False,,,,,,0,,,,Unknown,,,,,false,false,false,false,false,false,Susanne Gurman,verdasys,etl";

        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_admin_mark_true() {


        String testCase = "Test admin mark true";
        String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,powercfg.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,0,,,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,test@mail.com,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
        String expectedOutput = "2016-04-03 05:15:16,1459660516,sgurman,,,sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,powercfg.exe,0,0,,None,,,,,0,outlook,,,,test@mail.com,mail.com,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,,,true,False,,,,,,0,,,,Unknown,,,,,false,true,false,false,false,false,Susanne Gurman,verdasys,etl";

        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_regisrty_changed_mark_true() {


        String testCase = "Test registry changed mark true";
        String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,regedit.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,0,,,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,test@mail.com,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
        String expectedOutput = "2016-04-03 05:15:16,1459660516,sgurman,,,sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,regedit.exe,0,0,,None,,,,,0,outlook,,,,test@mail.com,mail.com,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,,,true,False,,,,,,0,,,,Unknown,,,,,false,false,true,false,false,false,Susanne Gurman,verdasys,etl";

        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

	@Test
	public void test_is_attachment_mail_event_field_in_case_of_messgae_body() {


		String testCase = "Test is_attachment_mail_event mark false";
		String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,regedit.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,0,,message body,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,test@mail.com,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
		String expectedOutput = "2016-04-03 05:15:16,1459660516,sgurman,,,sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,regedit.exe,0,0,,None,,message body,,,0,outlook,,,,test@mail.com,mail.com,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,,,true,False,,,,,,0,,,,Unknown,,,,,false,false,true,false,false,false,Susanne Gurman,verdasys,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_is_attachment_mail_event_field_in_case_of_empty_value_on_dest_file() {


		String testCase = "Test is_attachment_mail_event empty destination file mark false";
		String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,regedit.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,0,,,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,test@mail.com,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
		String expectedOutput = "2016-04-03 05:15:16,1459660516,sgurman,,,sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,regedit.exe,0,0,,None,,,,,0,outlook,,,,test@mail.com,mail.com,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,,,true,False,,,,,,0,,,,Unknown,,,,,false,false,true,false,false,false,Susanne Gurman,verdasys,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_has_src_classification_CALCULATION_true() {


		String testCase = "Test has_src_classification calculation";
		String inputLine = "2016.04.03,4/3/2016 9:20:07 PM,4/4/2016 1:20:07 AM,outlook.exe,verdasys\\arollins-e7450,Windows,,,,,,Ashlee,Rollins,A2591A52-25E6-4D65-B860-9A406D8C7E85,verdasys\\arollins,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,arollins@digitalguardian.com,Invoice 905 and 956,,AE8E5A08-761C-1034-92A3-806E7FDE1400,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,Outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,8011,0,c:\\users\\arollins\\appdata\\local\\microsoft\\windows\\inetcache\\content.word\\,isecurity - novatek taiwan mnt inv 956.pdf,,pdf,F32286A1-FA02-11E5-828D-4C348811383D,8011,False,isecurity.com.tw,cendy@isecurity.com.tw,To,,isecurity - novatek taiwan mnt inv 956.mdb,,c:\\users\\arollins\\appdata\\local\\microsoft\\windows\\inetcache\\content.word\\,isecurity - novatek taiwan mnt inv 956.mdb,,pdf,,False,False,True,0,0,False,False,False,Fixed,4ce49592-9453-00ae-144a-8e8d90c803cf,None,,arollins@digitalguardian.com,,,,,Unknown\n";
		String expectedOutput = "2016-04-04 01:20:07,1459732807,arollins,,,arollins-e7450,arollins-e7450,,,,,,,,,A2591A52-25E6-4D65-B860-9A406D8C7E85,,outlook.exe,8011,0,,None,c:\\users\\arollins\\appdata\\local\\microsoft\\windows\\inetcache\\content.word\\,isecurity - novatek taiwan mnt inv 956.pdf,,,8011,outlook,,,,cendy@isecurity.com.tw,isecurity.com.tw,,arollins@digitalguardian.com,Invoice 905 and 956,,Send Mail,,,AE8E5A08-761C-1034-92A3-806E7FDE1400,,,,true,true,False,,,,,isecurity - novatek taiwan mnt inv 956.mdb,0,,c:\\users\\arollins\\appdata\\local\\microsoft\\windows\\inetcache\\content.word\\,,Fixed,isecurity - novatek taiwan mnt inv 956.mdb,,,,false,false,false,true,,,Ashlee Rollins,dlpmail,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_has_src_classification_CALCULATION_false() {


		String testCase = "Test has_src_classification calculation";
		String inputLine = "2016.04.03,4/3/2016 9:20:07 PM,4/4/2016 1:20:07 AM,outlook.exe,verdasys\\arollins-e7450,Windows,,,,,,Ashlee,Rollins,A2591A52-25E6-4D65-B860-9A406D8C7E85,verdasys\\arollins,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,arollins@digitalguardian.com,Invoice 905 and 956,,AE8E5A08-761C-1034-92A3-806E7FDE1400,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,Outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,8011,0,c:\\users\\arollins\\appdata\\local\\microsoft\\windows\\inetcache\\content.word\\,isecurity - novatek taiwan mnt inv 956.pdf,,pdf,F32286A1-FA02-11E5-828D-4C348811383D,8011,False,isecurity.com.tw,cendy@isecurity.com.tw,To,,isecurity - novatek taiwan mnt inv 956.pdf,,c:\\users\\arollins\\appdata\\local\\microsoft\\windows\\inetcache\\content.word\\,isecurity - novatek taiwan mnt inv 956.pdf,,pdf,,False,False,True,0,0,False,False,False,Fixed,4ce49592-9453-00ae-144a-8e8d90c803cf,None,,arollins@digitalguardian.com,,,,,Unknown\n";
		String expectedOutput = "2016-04-04 01:20:07,1459732807,arollins,,,arollins-e7450,arollins-e7450,,,,,,,,,A2591A52-25E6-4D65-B860-9A406D8C7E85,,outlook.exe,8011,0,,None,c:\\users\\arollins\\appdata\\local\\microsoft\\windows\\inetcache\\content.word\\,isecurity - novatek taiwan mnt inv 956.pdf,,,8011,outlook,,,,cendy@isecurity.com.tw,isecurity.com.tw,,arollins@digitalguardian.com,Invoice 905 and 956,,Send Mail,,,AE8E5A08-761C-1034-92A3-806E7FDE1400,,,,false,true,False,,,,,isecurity - novatek taiwan mnt inv 956.pdf,0,,c:\\users\\arollins\\appdata\\local\\microsoft\\windows\\inetcache\\content.word\\,,Fixed,isecurity - novatek taiwan mnt inv 956.pdf,,,,false,false,false,true,,,Ashlee Rollins,dlpmail,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}




}

