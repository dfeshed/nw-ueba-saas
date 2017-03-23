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
				.setIsExternal("true")
				.setNumOfRecipients("0")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the domain - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the domain - this is ok for this test
				// interesting test stuff starts here
				.setHostname("example_hostname")
				.setNormalizedSrcMachine("example_hostname")
				.setUsername("example_username")
				.createDgEventAfterEtl();


		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
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
				.setIsExternal("true")
				.setNumOfRecipients("0")
				.setDataSource("dlpmail")
				.setLastState("etl")
				// interesting test stuff starts here
				.setEmailSender("some sender")
				.setEmailRecipient("some_emailRecipient@somedomain.com") // because the parsing wont find the domain - this is ok for this test
				.setEmailRecipientDomain("somedomain.com") // because the parsing wont find the domain - this is ok for this test
				.createDgEventAfterEtl();


		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
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
				.setIsExternal("true")
				.setNumOfRecipients("0")
				.setDataSource("dlpmail")
				.setLastState("etl")
				// interesting test stuff starts here
				.setEmailRecipient("some_emailRecipient@somedomain.com") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("somedomain.com") // because the parsing wont find the @ - this is ok for this test
				.createDgEventAfterEtl();

		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
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
				.setIsExternal("true")
				.setNumOfRecipients("0")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setFullName("some_givenName some_surname")
				.createDgEventAfterEtl();

		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@Test
	public void test_empty_replace_attachment_file_size_with_zero() {
		String testCase = "Test that empty values in 'attachment_file_size' field  are replaced with '0'";
		DgEventInput input = new DgEventInputBuilder()
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
				.setNumOfRecipients("0")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDetailFileSize("0")
				.createDgEventAfterEtl();

		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
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
				.setNumOfRecipients("0")
				.setDataSource("dlpmail")
				.setLastState("etl")
				// interesting test stuff starts here
				.setIsExternal("true")
				.createDgEventAfterEtl();


		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
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
				.setNumOfRecipients("0")
				.setDataSource("dlpmail")
				.setLastState("etl")
				// interesting test stuff starts here
				.setIsExternal("false")
				.createDgEventAfterEtl();


		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
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
				.setIsExternal("true")
				.setNumOfRecipients("0")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("")
				.setEventType("recipient")
				.createDgEventAfterEtl();

		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
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
				.setIsExternal("true")
				.setNumOfRecipients("0")
				.setDataSource("dlpmail")
				.setLastState("etl")
				.setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				.setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
				// interesting test stuff starts here
				.setDestinationFile("somefile.jpg")
				.setEventType("attachment")
				.createDgEventAfterEtl();

		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
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

		final String inputLine = input.toString();
		final String expectedOutput = expected.toString();
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

}






