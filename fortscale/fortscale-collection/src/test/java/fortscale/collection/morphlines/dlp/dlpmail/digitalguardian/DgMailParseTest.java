package fortscale.collection.morphlines.dlp.dlpmail.digitalguardian;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context-test-light.xml"})
@Ignore
public class DgMailParseTest {


	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private static String DUMMY_EVENT_STRING = ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,Fortscale Control,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";

	@Before
	public void setUp() throws Exception {

		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-collection-test.properties");
		String kafkaMessageFields = propertiesResolver.getProperty("kafka.dlpmail.message.record.fields");

		List<String> outputFields = ImpalaParser.getTableFieldNames(kafkaMessageFields);
		String confFile = "resources/conf-files/parseDGMail.conf";
		String confEnrichmentFile = "resources/conf-files/enrichment/readDlpMail_enrich.conf";
		morphlineTester.init(new String[]{confFile, confEnrichmentFile}, outputFields);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}


	private void runOneLineTestWithDummyEvent(String testCase, DgEventInput input, DgMailEventAfterEtl expected) {
		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		if (expected.eventType.equals("attachment")) {
			morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
		}
		else {
			morphlineTester.runMorphlines(inputLine);
			morphlineTester.testSingleLine(testCase, DUMMY_EVENT_STRING, expectedOutput);
		}
	}


	@Test
	public void test_parse_file() {
		String testCase = "Test the parsing (see if the fields are where we expect them)";
		final String inputLine = "2016.06.12,06/12/2016 11:04,06/12/2016 16:04,outlook.exe,keizer-vm-w81,Windows,,,,,,Robert,Keizer,18E1DA6C-D322-1B41-A10C-E4FCD9446E5A,keizer,,microsoft,F022AD53C4EF895E6BA8E5D5BB57C9C11984D7A8,11F98624A0150CE5D067B851EF13E7E9EAE1F09260B1FEBFAEA8D3CD013C0DB8,microsoft outlook,15.0.4823.1000,Scanned,05/12/2016 12:37,Virus Total: 0 / 56 scans positive.,,,,,outlook,rkeizer@digitalguardian.com,meeting minutes,,6F260640-F4E2-1037-2E6E-806E7F2DFDB9,10.9.9.9,,3540464d922ae137393c446234015b4e,4d464035-2a92-37e1-393c-446234015b4e,Outbound,Send Mail,,0,,FALSE,FALSE,FALSE,FALSE,FALSE,TRUE,1,FALSE,0,FALSE,FALSE,FALSE,,,,,,,removal policy,,,,,,Not Blocked,286694,0,c:\\users\\rkeizer\\documents\\hr\\forms\\,ppo enroll form - signed.pdf,,pdf,0BC5E66B-30B7-11E6-8279-C869CD986175,286694,FALSE,digitalguardian.com,jmurnane@digitalguardian.com,To,,ppo enroll form - signed.pdf,,c:\\users\\rkeizer\\documents\\hr\\forms\\,ppo enroll form - signed.pdf,,pdf,,FALSE,FALSE,TRUE,0,0,FALSE,FALSE,FALSE,Fixed,c09427ce-3153-ea9f-8483-f28441f4669f,None,,rkeizer@digitalguardian.com,,,,,Unknown";

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setEventType("attachment")
				.setEventId("6F260640-F4E2-1037-2E6E-806E7F2DFDB9")
				.setUsername("keizer")
				.setFullName("Robert Keizer")
				.setIpAddress("10.9.9.9")
				.setHostname("keizer-vm-w81")
				.setNormalizedSrcMachine("keizer-vm-w81")
				.setExecutingApplication("outlook.exe")
				.setDestinationFile("ppo enroll form - signed.pdf")
				.setDetailFileSize(286694)
				.setDestinationDirectory("c:\\users\\rkeizer\\documents\\hr\\forms\\")
				.setDestinationFileExtension("pdf")
				.setIsAttachmentExtensionBlacklisted("false")
				.setEmailRecipient("jmurnane@digitalguardian.com")
				.setEmailRecipientDomain("digitalguardian.com")
				.setEmailSender("rkeizer@digitalguardian.com")
				.setEmailSubject("meeting minutes") //supposed to be empty in 'attachment' event types but i edited the line for the sake of the test
				.setIsExternal(false)
				.setNumOfRecipients(0)
				.setWasClassified("FALSE")
				.setWasBlocked("FALSE")
				.setScanValueStatusText("Virus Total: 0 / 56 scans positive.")
				.setPolicyName("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.createDgEventAfterEtl();
		morphlineTester.testSingleLine(testCase, inputLine, expected.toString());
	}

	@Test
	public void test_empty_fields_filter() {
		String testCase = "Test filter empty events";
		DgEventInput input = new DgEventInputBuilder().createDgEvent(); // event with all fields
		// interesting test stuff starts here
		input.eventId = ""; //we empty one of the required fields

		final String inputLine = input.toString();
		morphlineTester.testSingleLineFiltered(testCase, inputLine);
	}

	@Test
	public void test_filter_not_send_mail() {
		String testCase = "Test filter not \"Send Mail\" events";
		DgEventInput input = new DgEventInputBuilder()
				// interesting test stuff starts here
				.setOperation("something that is not Send Mail")
				.createDgEvent(); // event with all fields

		final String inputLine = input.toString();
		morphlineTester.testSingleLineFiltered(testCase, inputLine);
	}

	@Test
	@Ignore
	public void test_remove_verdasys_prefix() {
		String testCase = "Test that the verdasys\r prefix is removed";
		DgEventInput input = new DgEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				// interesting test stuff starts here
				.setComputerName("verdasys\rexample_hostname")
				.setUsername("verdasys\rexample_username")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setEventType("attachment")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal(true)
				.setNumOfRecipients(0)
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the domain - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the domain - this is ok for this test
				// interesting test stuff starts here
				.setHostname("example_hostname")
				.setNormalizedSrcMachine("example_hostname")
				.setUsername("example_username")
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_remove_quotes_from_email_sender_and_recipient() {
		String testCase = "Test that quotes are removed from email sender and recipient";
		DgEventInput input = new DgEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				// interesting test stuff starts here
				.setEmailSender("'some sender'")
				.setEmailRecipient("'some_emailRecipient@somedomain.com'")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setEventType("attachment")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal(true)
				.setNumOfRecipients(0)
				.setDataSource("dlpmail")
				.setLastState("etl")
				// interesting test stuff starts here
				.setEmailSender("some sender")
				.setEmailRecipient("some_emailRecipient@somedomain.com") // because the parsing wont find the domain - this is ok for this test
				.setEmailRecipientDomain("somedomain.com") // because the parsing wont find the domain - this is ok for this test
				.createDgEventAfterEtl();


		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_remove_extract_recipient_domain() {
		String testCase = "Test that email_recipient_domain is extracted correctly from email_recipient";
		DgEventInput input = new DgEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				// interesting test stuff starts here
				.setEmailRecipient("some_emailRecipient@somedomain.com")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setEventType("attachment")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal(true)
				.setNumOfRecipients(0)
				.setDataSource("dlpmail")
				.setLastState("etl")
				// interesting test stuff starts here
				.setEmailRecipient("some_emailRecipient@somedomain.com") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("somedomain.com") // because the parsing wont find the @ - this is ok for this test
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_remove_extract_full_name() {
		String testCase = "Test that full_name is extracted from first_name and surname";
		DgEventInput input = new DgEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setEventType("attachment")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal(true)
				.setNumOfRecipients(0)
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setFullName("some_givenName some_surname")
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_empty_replace_attachment_file_size_with_zero() {
		String testCase = "Test that empty values in 'attachment_file_size' field  are replaced with '0'";
		DgEventInput input = new DgEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				// interesting test stuff starts here
				.setDetailFileSize(0)
				.setDetailFileSize(500)
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setEventType("attachment")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal(true)
				.setNumOfRecipients(0)
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDetailFileSize(0)
				.setDetailFileSize(500)
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_is_external_true() {
		String testCase = "Test is_external is true";
		DgEventInput input = new DgEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				// interesting test stuff starts here
				.setEmailRecipient("somename@externaldomain.com")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setEventType("attachment")
				.setIsAttachmentExtensionBlacklisted("false")
				.setEmailRecipient("somename@externaldomain.com")
				.setEmailRecipientDomain("externaldomain.com")
				.setNumOfRecipients(0)
				.setDataSource("dlpmail")
				.setLastState("etl")
				// interesting test stuff starts here
				.setIsExternal(true)
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_is_external_false() {
		String testCase = "Test is_external is False";
		DgEventInput input = new DgEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				// interesting test stuff starts here
				.setEmailRecipient("somename@digitalguardian.com")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setEventType("attachment")
				.setIsAttachmentExtensionBlacklisted("false")
				.setEmailRecipient("somename@digitalguardian.com")
				.setEmailRecipientDomain("digitalguardian.com")
				.setNumOfRecipients(0)
				.setDataSource("dlpmail")
				.setLastState("etl")
				// interesting test stuff starts here
				.setIsExternal(false)
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_event_type_recipient() {
		String testCase = "Test events with attachment_file_name=\"message body\" - attachment_file_name should be cleared and event is marked as 'recipient'";
		DgEventInput input = new DgEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				.setDestinationFile("message body")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal(true)
				.setNumOfRecipients(1)
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("")
				.setEventType("recipient")
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_event_type_attachment() {
		String testCase = "Test events with (attachment_file_name!=\"message body\" && attachment_file_name!=\"\") are marked as 'attachment'";
		DgEventInput input = new DgEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				.setDestinationFile("somefile.jpg")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal(true)
				.setNumOfRecipients(0)
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("somefile.jpg")
				.setEventType("attachment")
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_event_type_message_body() {
		String testCase = "Test events with (attachment_file_name==\"\") are marked as 'message body' (and also the attachment_file_name=\"message body\")";
		DgEventInput input = new DgEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				.setDestinationFile("")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal(true)
				.setNumOfRecipients(0)
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("message_body")
				.setEventType("message_body")
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

}






