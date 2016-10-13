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
public class DGVerdasysTest {


	private MorphlinesTester morphlineTester = new MorphlinesTester();


	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass() {

	}

	@Before
	public void setUp() throws Exception {


		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-collection-test.properties");
		String kafkaMessageFields = propertiesResolver.getProperty("kafka.verdasys_exchange.message.record.fields");

		List<String> sshMessageOutputFields = ImpalaParser.getTableFieldNames(kafkaMessageFields);
		String confFile = "resources/conf-files/parseVERDASYSEX.conf";
		String confEnrichmentFile = "resources/conf-files/enrichment/readVERDASYSEX_enrich.conf";
		morphlineTester.init(new String[]{confFile, confEnrichmentFile}, sshMessageOutputFields);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}


	@Test
	public void test_Outbound_uppercase_mapped_to_true() {


		String testCase = "Test Outbound uppercase replacement";
		String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,outlook.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,Outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,0,,,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
		String expectedOutput = "2016-04-03 05:15:16,1459660516,verdasys\\sgurman,,,verdasys\\sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,outlook.exe,0,0,,None,,,0,outlook,,,,,,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,EB2F1191-EF9B-4CE6-9857-EF64518CA759,,FD1FB403-744B-1034-4B92-806E2F77B91B,,False,,False,true,,,,,,,0,,,,Unknown,,,False,false,verdasys_exchange,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_outbound_lowercase_mapped_to_true() {


		String testCase = "Test inbound out bound replacement";
		String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,outlook.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,outbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,0,,,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
		String expectedOutput = "2016-04-03 05:15:16,1459660516,verdasys\\sgurman,,,verdasys\\sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,outlook.exe,0,0,,None,,,0,outlook,,,,,,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,EB2F1191-EF9B-4CE6-9857-EF64518CA759,,FD1FB403-744B-1034-4B92-806E2F77B91B,,False,,False,true,,,,,,,0,,,,Unknown,,,False,false,verdasys_exchange,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_not_outbound_mapped_to_false() {


		String testCase = "Test inbound out bound replacement";
		String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,outlook.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,inbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Not Blocked,0,0,,,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
		String expectedOutput = "2016-04-03 05:15:16,1459660516,verdasys\\sgurman,,,verdasys\\sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,outlook.exe,0,0,,None,,,0,outlook,,,,,,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,EB2F1191-EF9B-4CE6-9857-EF64518CA759,,FD1FB403-744B-1034-4B92-806E2F77B91B,,False,,False,false,,,,,,,0,,,,Unknown,,,False,false,verdasys_exchange,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_empty_was_blocked_mapped_to_falase() {


		String testCase = "Test inbound out bound replacement";
		String inputLine = "2016.04.29,4/29/2016 1:54:31 PM,4/29/2016 8:54:31 PM,outlook.exe,verdasys\\PRILEY-E6330,Windows,,,,,,Pat,Riley,C76742A7-B423-E64A-8E7E-309F77D17A40,verdasys\\priley,,microsoft,2505FA905BABE407B4A941E9D8A1C4E4265A3A77,0C0CA078AE55E9A98F0F7F22C6ED6CF6B42D150FA01301D5F53E2476EB499A51,microsoft outlook,14.0.7168.5000,Scanned,4/7/2016 3:28:50 PM,Virus Total: 0 / 57 scans positive.,,,,,outlook,priley@digitalguardian.com,Net New Logo Bonus Question,,C6F82231-A421-1037-8E17-806E2FE60570,,,8318a11035bd5ddc1956f2360d4cace6,10a11883-bd35-dc5d-1956-f2360d4cace6,Outbound,Send Mail,,0,,False,False,False,True,False,True,0,False,0,False,False,False,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,priley@digitalguardian.com,,,,,";
		String expectedOutput = "2016-04-29 20:54:31,1461963271,verdasys\\priley,,,verdasys\\PRILEY-E6330,,,,,,,,,,C76742A7-B423-E64A-8E7E-309F77D17A40,,outlook.exe,,,,,,,,outlook,,,,,,,priley@digitalguardian.com,Net New Logo Bonus Question,,Send Mail,,,C6F82231-A421-1037-8E17-806E2FE60570,,False,,,true,,,,,,,0,,,,,,,False,false,verdasys_exchange,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_was_blocked_mapped_to_true() {


		String testCase = "Test inbound out bound replacement";
		String inputLine = "2016.04.03,4/3/2016 1:15:16 AM,4/3/2016 5:15:16 AM,outlook.exe,verdasys\\sgurman-e7440,Windows,,,,,,Susanne,Gurman,96479FB8-A87B-AD46-82FA-10FD1680EF8A,verdasys\\sgurman,,microsoft,4A7E05E6B884634865B46C183398C2B0308C2B06,C15B3AC1EDA1B6CA671526FE00F88804AFAD96DD8E7699854C027548415114D6,microsoft outlook,15.0.4805.1000,Scanned,3/9/2016 3:54:25 AM,Virus Total: 0 / 57 scans positive.,,,,,outlook,sgurman@digitalguardian.com,RUSH - The board is asking!,,FD1FB403-744B-1034-4B92-806E2F77B91B,,,5ed6ecbd5a82c562188ce3256f68e923,bdecd65e-825a-62c5-188c-e3256f68e923,inbound,Send Mail,,0,,False,False,False,True,False,True,1,False,0,False,False,False,,,,,,,,,,,,,Blocked,0,0,,,,,EB2F1191-EF9B-4CE6-9857-EF64518CA759,0,False,,,,,,,,,,BLANK_EXTENSION,,False,False,False,0,0,False,False,False,Unknown,,None,,sgurman@digitalguardian.com,,,,,";
		String expectedOutput = "2016-04-03 05:15:16,1459660516,verdasys\\sgurman,,,verdasys\\sgurman-e7440,,,,,,,,,,96479FB8-A87B-AD46-82FA-10FD1680EF8A,,outlook.exe,0,0,,None,,,0,outlook,,,,,,,sgurman@digitalguardian.com,RUSH - The board is asking!,,Send Mail,EB2F1191-EF9B-4CE6-9857-EF64518CA759,,FD1FB403-744B-1034-4B92-806E2F77B91B,,False,,False,false,,,,,,,0,,,,Unknown,,,False,true,verdasys_exchange,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_replace_empty_value_to_0_bytes_written() {


		String testCase = "Test empty value bytes_written";
		String inputLine = "2016.04.21,4/21/2016 4:39:07 PM,4/21/2016 11:39:07 PM,outlook.exe,verdasys\\PRILEY-E6330,Windows,,,,,,Pat,Riley,C76742A7-B423-E64A-8E7E-309F77D17A40,verdasys\\priley,,microsoft,2505FA905BABE407B4A941E9D8A1C4E4265A3A77,0C0CA078AE55E9A98F0F7F22C6ED6CF6B42D150FA01301D5F53E2476EB499A51,microsoft outlook,14.0.7168.5000,Scanned,4/7/2016 3:28:50 PM,Virus Total: 0 / 57 scans positive.,,,,,outlook,priley@digitalguardian.com,RE: DBR - Security Recruiters,,6C74A982-9A5E-1037-F62B-806E7F7E7470,,,8318a11035bd5ddc1956f2360d4cace6,10a11883-bd35-dc5d-1956-f2360d4cace6,Outbound,Send Mail,,0,,False,False,False,True,False,True,0,False,0,False,False,False,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,priley@digitalguardian.com,,,,,\n";
		String expectedOutput = "2016-04-21 23:39:07,1461281947,priley,,,PRILEY-E6330,PRILEY-E6330,,,,,,,,,C76742A7-B423-E64A-8E7E-309F77D17A40,,outlook.exe,,false,,,,,,outlook,,,,,,,priley@digitalguardian.com,RE: DBR - Security Recruiters,,Send Mail,,,6C74A982-9A5E-1037-F62B-806E7F7E7470,,False,,,true,false,,,,,,0,,,,,,,False,false,verdex,etl";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

}

