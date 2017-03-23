package fortscale.collection.morphlines.dlpmail.digitalguardian;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context-test-light.xml"})
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


	private void runOneLineTestWithDummyEvent(String testCase, DgMailEventInput input, DgMailEventAfterEtl expected) {
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
		final String inputLine = "2016.06.12,06/12/2016 11:04,06/12/2016 16:04,outlook.exe,verdasys\\rkeizer-vm-w81,Windows,,,,,,Robert,Keizer,18E1DA6C-D322-1B41-A10C-E4FCD9446E5A,verdasys\\rkeizer,,microsoft,F022AD53C4EF895E6BA8E5D5BB57C9C11984D7A8,11F98624A0150CE5D067B851EF13E7E9EAE1F09260B1FEBFAEA8D3CD013C0DB8,microsoft outlook,15.0.4823.1000,Scanned,05/12/2016 12:37,Virus Total: 0 / 56 scans positive.,,,,,outlook,rkeizer@digitalguardian.com,meeting minutes,,6F260640-F4E2-1037-2E6E-806E7F2DFDB9,10.9.9.9,,3540464d922ae137393c446234015b4e,4d464035-2a92-37e1-393c-446234015b4e,Outbound,Send Mail,,0,,FALSE,FALSE,FALSE,FALSE,FALSE,TRUE,1,FALSE,0,FALSE,FALSE,FALSE,,,,,,,removal policy,,,,,,Not Blocked,286694,0,c:\\users\\rkeizer\\documents\\hr\\forms\\,ppo enroll form - signed.pdf,,pdf,0BC5E66B-30B7-11E6-8279-C869CD986175,286694,FALSE,digitalguardian.com,jmurnane@digitalguardian.com,To,,ppo enroll form - signed.pdf,,c:\\users\\rkeizer\\documents\\hr\\forms\\,ppo enroll form - signed.pdf,,pdf,,FALSE,FALSE,TRUE,0,0,FALSE,FALSE,FALSE,Fixed,c09427ce-3153-ea9f-8483-f28441f4669f,None,,rkeizer@digitalguardian.com,,,,,Unknown";

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setEventType("attachment")
				.setEventId("6F260640-F4E2-1037-2E6E-806E7F2DFDB9")
				.setUsername("verdasys\\rkeizer")
				.setFullName("Robert Keizer")
				.setIpAddress("10.9.9.9")
				.setHostname("verdasys\\rkeizer-vm-w81")
				.setNormalizedSrcMachine("verdasys\\rkeizer-vm-w81")
				.setApplication("outlook.exe")
				.setDestinationFile("ppo enroll form - signed.pdf")
				.setDetailFileSize("286694")
				.setDestinationDirectory("c:\\users\\rkeizer\\documents\\hr\\forms\\")
				.setDestinationFileExtension("pdf")
				.setIsAttachmentExtensionBlacklisted("false")
				.setEmailRecipient("jmurnane@digitalguardian.com")
				.setEmailRecipientDomain("digitalguardian.com")
				.setEmailSender("rkeizer@digitalguardian.com")
				.setEmailSubject("meeting minutes") //supposed to be empty in 'attachment' event types but i edited the line for the sake of the test
				.setIsExternal("false")
				.setNumOfRecipients("0")
				.setWasClassified("FALSE")
				.setWasBlocked("FALSE")
				.setScanValueStatusText("Virus Total: 0 / 56 scans positive.")
				.setPolicyName("removal policy")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.createDgEventAfterEtl();
		morphlineTester.testSingleLine(testCase, inputLine, expected.toString());
	}

	@Test
	public void test_empty_fields_filter() {
		String testCase = "Test filter empty events";
		DgMailEventInput input = new DgMailEventInputBuilder().createDgEvent(); // event with all fields
		// interesting test stuff starts here
		input.eventId = ""; //we empty one of the required fields

		final String inputLine = input.toString();
		morphlineTester.testSingleLineFiltered(testCase, inputLine);
	}

	@Test
	public void test_filter_not_send_mail() {
		String testCase = "Test filter not \"Send Mail\" events";
		DgMailEventInput input = new DgMailEventInputBuilder()
				// interesting test stuff starts here
				.setOperation("something that is not Send Mail")
				.createDgEvent(); // event with all fields

		final String inputLine = input.toString();
		morphlineTester.testSingleLineFiltered(testCase, inputLine);
	}

	@Test
	public void test_remove_verdasys_prefix() {
		String testCase = "Test that the verdasys\r prefix is removed";
		DgMailEventInput input = new DgMailEventInputBuilder()
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
				.setIsExternal("true")
				.setNumOfRecipients("")
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
		DgMailEventInput input = new DgMailEventInputBuilder()
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
				.setIsExternal("true")
				.setNumOfRecipients("")
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
		DgMailEventInput input = new DgMailEventInputBuilder()
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
				.setIsExternal("true")
				.setNumOfRecipients("")
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
		DgMailEventInput input = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setEventType("attachment")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("")
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
		DgMailEventInput input = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setOperation("Send Mail")
				// interesting test stuff starts here
				.setDetailFileSize("")
				.createDgEvent();

		DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setEventType("attachment")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDetailFileSize("0")
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_is_external_true() {
		String testCase = "Test is_external is true";
		DgMailEventInput input = new DgMailEventInputBuilder()
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
				.setNumOfRecipients("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				// interesting test stuff starts here
				.setIsExternal("true")
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_is_external_false() {
		String testCase = "Test is_external is False";
		DgMailEventInput input = new DgMailEventInputBuilder()
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
				.setNumOfRecipients("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				// interesting test stuff starts here
				.setIsExternal("false")
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_event_type_recipient() {
		String testCase = "Test events with attachment_file_name=\"message body\" - attachment_file_name should be cleared and event is marked as 'recipient'";
		DgMailEventInput input = new DgMailEventInputBuilder()
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
				.setIsExternal("true")
				.setNumOfRecipients("1")
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
		DgMailEventInput input = new DgMailEventInputBuilder()
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
				.setIsExternal("true")
				.setNumOfRecipients("")
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
		DgMailEventInput input = new DgMailEventInputBuilder()
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
				.setIsExternal("true")
				.setNumOfRecipients("0")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("message body")
				.setEventType("message body")
				.createDgEventAfterEtl();

		runOneLineTestWithDummyEvent(testCase, input, expected);
	}

	@Test
	public void test_num_of_recipients_multiple_events_with_closing_dummy_event() throws Exception {
		/* ******************************************************************* event id = aaa ***************************************************************************/

		DgMailEventInput input1 = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setEventId("aaa")
				.setOperation("Send Mail")
				.setDestinationFile("somefile.jpg") //attachment
				.createDgEvent();

		DgMailEventAfterEtl expected1 = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setEventId("aaa")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("somefile.jpg")
				.setEventType("attachment")
				.createDgEventAfterEtl();

		DgMailEventInput input2 = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setEventId("aaa")
				.setOperation("Send Mail")
				.setDestinationFile("") //message body
				.createDgEvent();

		DgMailEventAfterEtl expected2 = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setEventId("aaa")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("1")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("message body")
				.setEventType("message body")
				.createDgEventAfterEtl();

		DgMailEventInput input3 = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setEventId("aaa")
				.setOperation("Send Mail")
				.setDestinationFile("message body") //recipient
				.createDgEvent();

		DgMailEventAfterEtl expected3 = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setEventId("aaa")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("")
				.setEventType("recipient")
				.createDgEventAfterEtl();



		/* ******************************************************************* event id = bbb ***************************************************************************/

		DgMailEventInput input4 = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setEventId("bbb")
				.setOperation("Send Mail")
				.setDestinationFile("") //message body
				.createDgEvent();

		DgMailEventAfterEtl expected4 = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setEventId("bbb")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("2")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("message body")
				.setEventType("message body")
				.createDgEventAfterEtl();

		DgMailEventInput input5 = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setEventId("bbb")
				.setOperation("Send Mail")
				.setDestinationFile("somefile.jpg") //attachment
				.createDgEvent();

		DgMailEventAfterEtl expected5 = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setEventId("bbb")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("somefile.jpg")
				.setEventType("attachment")
				.createDgEventAfterEtl();

		DgMailEventInput input6 = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setEventId("bbb")
				.setOperation("Send Mail")
				.setDestinationFile("message body") //recipient
				.createDgEvent();

		DgMailEventAfterEtl expected6 = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setEventId("bbb")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("")
				.setEventType("recipient")
				.createDgEventAfterEtl();

		DgMailEventInput input7 = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setEventId("bbb")
				.setOperation("Send Mail")
				.setDestinationFile("message body") //recipient
				.createDgEvent();

		DgMailEventAfterEtl expected7 = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setEventId("bbb")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("")
				.setEventType("recipient")
				.createDgEventAfterEtl();


/* ******************************************************************* event id = ccc ***************************************************************************/



		DgMailEventInput input8 = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setEventId("ccc")
				.setOperation("Send Mail")
				.setDestinationFile("somefile.jpg") //attachment
				.createDgEvent();

		DgMailEventAfterEtl expected8 = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setEventId("ccc")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("somefile.jpg")
				.setEventType("attachment")
				.createDgEventAfterEtl();

		DgMailEventInput input9 = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setEventId("ccc")
				.setOperation("Send Mail")
				.setDestinationFile("message body") //recipient
				.createDgEvent();

		DgMailEventAfterEtl expected9 = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setEventId("ccc")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("")
				.setEventType("recipient")
				.createDgEventAfterEtl();

		DgMailEventInput input10 = new DgMailEventInputBuilder()
				.setAgentUtcTime("06/12/2016 16:04")
				.setEventId("ccc")
				.setOperation("Send Mail")
				.setDestinationFile("") //message body
				.createDgEvent();

		DgMailEventAfterEtl expected10 = new DgMailEventAfterEtlBuilder()
				.setDateTime("2016-06-12 16:04:00")
				.setEventId("ccc")
				.setDateTimeUnix("1465747440")
				.setEventDescription("Send Mail")
				.setFullName("some_givenName some_surname")
				.setIsAttachmentExtensionBlacklisted("false")
				.setIsExternal("true")
				.setNumOfRecipients("1")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("message body")
				.setEventType("message body")
				.createDgEventAfterEtl();

		/* ****************************************************************************************************************************************************/

		/* eventId = aaa */
		morphlineTester.testSingleLine("1", input1.toString(), expected1.toString()); //id aaa - attachment
		morphlineTester.testSingleLine("2", input2.toString(), null);  //id aaa - message body
		morphlineTester.testSingleLine("3", input3.toString(), expected3.toString()); //id aaa - recipient

		/* eventId = bbb */
		morphlineTester.testSingleLine("4", input4.toString(), expected2.toString()); //id bbb - message body
		morphlineTester.testSingleLine("5", input5.toString(), expected5.toString()); //id bbb - attachment
		morphlineTester.testSingleLine("6", input6.toString(), expected6.toString()); //id bbb - recipient
		morphlineTester.testSingleLine("7", input7.toString(), expected7.toString()); //id bbb - recipient

		/* eventId = ccc */
		morphlineTester.testSingleLine("8", input8.toString(), expected8.toString()); //id ccc - attachment
		morphlineTester.testSingleLine("10", input9.toString(), expected4.toString());//id ccc - recipient
		morphlineTester.testSingleLine("9", input10.toString(), expected9.toString());  //id ccc - message body


		morphlineTester.testSingleLine("11", DUMMY_EVENT_STRING, expected10.toString()); //dummy


	}
}






