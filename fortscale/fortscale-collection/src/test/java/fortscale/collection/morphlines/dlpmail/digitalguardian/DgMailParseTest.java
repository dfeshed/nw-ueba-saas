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



		/* ****************************************************************************************************************************************************/


		morphlineTester.testSingleLine("1", input1.toString(), expected1.toString()); //id aaa - attachment
		morphlineTester.testSingleLine("2", input2.toString(), null); //id aaa - message body
		morphlineTester.testSingleLine("3", input3.toString(), expected3.toString()); //id aaa - recipient

		morphlineTester.testSingleLine("4", input4.toString(), expected2.toString()); //id bbb - msg
		morphlineTester.testSingleLine("5", input5.toString(), expected5.toString()); //id bbb - attachment
		morphlineTester.testSingleLine("6", input6.toString(), expected6.toString()); //id bbb - rec
		morphlineTester.testSingleLine("7", input7.toString(), expected7.toString()); //id bbb - rec
		morphlineTester.testSingleLine("8", DUMMY_EVENT_STRING, expected4.toString()); //dummy


	}
}






