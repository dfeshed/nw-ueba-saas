package fortscale.collection.morphlines.dlp.dlpmail;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.dlp.dlpmail.digitalguardian.DgEventInput;
import fortscale.collection.morphlines.dlp.dlpmail.digitalguardian.DgEventInputBuilder;
import fortscale.collection.morphlines.dlp.dlpmail.digitalguardian.DgMailEventAfterEtl;
import fortscale.collection.morphlines.dlp.dlpmail.digitalguardian.DgMailEventAfterEtlBuilder;
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
public class DlpMailEnrichTest {

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


    @Test
    public void test_normalized_src_machine_is_like_hostname() {
        String testCase = "Test that normalized src machine is set to be like hostname field";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("Send Mail")
                .setComputerName("example_hostname")
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
                .setEventType("attachment")
                // interesting test stuff starts here
                .setHostname("example_hostname") // just to be explicit
                .setNormalizedSrcMachine("example_hostname")
                .createDgEventAfterEtl();

        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_is_attachment_blacklisted_false() {
        String testCase = "Test that is_attachment_blacklisted is false if extension isn't blacklisted";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("Send Mail")
                .setDestinationFileExtension("jpeg")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEventType("attachment")
                // interesting test stuff starts here
                .setDestinationFileExtension("jpeg")
                .setIsAttachmentExtensionBlacklisted("false")
                .createDgEventAfterEtl();

        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_is_attachment_blacklisted_true() {
        String testCase = "Test that is_attachment_blacklisted is true if extension is blacklisted";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("Send Mail")
                .setDestinationFileExtension("wdb")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEventType("attachment")
                // interesting test stuff starts here
                .setDestinationFileExtension("wdb")
                .setIsAttachmentExtensionBlacklisted("true")
                .createDgEventAfterEtl();

        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_data_source() {
        String testCase = "Test that the data_source is dlpmail";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("Send Mail")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some" +
                        "_surname")
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setLastState("etl")
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEventType("attachment")
                .setIsAttachmentExtensionBlacklisted("false")
                // interesting test stuff starts here
                .setDataSource("dlpmail")
                .createDgEventAfterEtl();

        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_last_state() {
        String testCase = "Test that the last_state is etl";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("Send Mail")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEventType("attachment")
                .setIsAttachmentExtensionBlacklisted("false")
                .setDataSource("dlpmail")
                // interesting test stuff starts here
                .setLastState("etl")
                .createDgEventAfterEtl();

        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_dates_formatting() {
        String testCase = "Test dates formatting";
        DgEventInput input = new DgEventInputBuilder()
                .setOperation("Send Mail")
                // interesting test stuff starts here
                .setAgentUtcTime("06/12/2016 16:04")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setEventType("attachment")
                .setIsAttachmentExtensionBlacklisted("false")
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the domain - this is ok for this test
                // interesting test stuff starts here
                .setDateTimeUnix("1465747440")
                .setDateTime("2016-06-12 16:04:00")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_device_entities() {
        String testCase = "Test DEVICE entities";
        DgEventInput input = new DgEventInputBuilder()
                .setOperation("Send Mail")
                .setAgentUtcTime("06/12/2016 16:04")
                // interesting test stuff starts here
                .setUsername("NT AUTHORITY\\LOCAL SERVICE")
                .setComputerName("example_hostname")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTimeUnix("1465747440")
                .setDateTime("2016-06-12 16:04:00")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setEventType("attachment")
                .setIsAttachmentExtensionBlacklisted("false")
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the domain - this is ok for this test
                // interesting test stuff starts here
                .setUsername("Device\\example_hostname")
                .setHostname("example_hostname")
                .setNormalizedSrcMachine("")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_set_event_type_attachment() {
        String testCase = "Test that event type is attachment for events that are \"Attach Mail\" OR \"Send Mail\" with (attachment_file_name!=\"\" | \"message body\"";
        DgEventInput input = new DgEventInputBuilder()
                .setOperation("Send Mail")
                .setAgentUtcTime("06/12/2016 16:04")
                .setDestinationFile("somefile.jpeg")
                // interesting test stuff starts here
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTimeUnix("1465747440")
                .setDateTime("2016-06-12 16:04:00")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setIsAttachmentExtensionBlacklisted("false")
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the domain - this is ok for this test
                // interesting test stuff starts here
                .setDestinationFile("somefile.jpeg")
                .setEventType("attachment")
                .createDgEventAfterEtl();

        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        //TODO:
        /* we are currently filtering "Attach Mail" events because we only support DG events (when it comes to parsing) so we ignore this part for now */

//        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
//                .setDateTimeUnix("1465747440")
//                .setDateTime("2016-06-12 16:04:00")
//                .setEventDescription("Attach Mail")
//                .setFullName("some_givenName some_surname")
//                .setIsAttachmentExtensionBlacklisted("false")
//                .setIsExternal(true)
//                .setDataSource("dlpmail")
//                .setLastState("etl")
//                .setEventType("attachment")
//                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the domain - this is ok for this test
//                // interesting test stuff starts here
//                .setNumOfRecipients(0)
//                .createDgEventAfterEtl();

//        final String inputLine2 = input2.toString();
//        final String expectedOutput2 = expected2.toString();
//        morphlineTester.testSingleLine(testCase, inputLine2, expectedOutput2);
    }

    @Test
    public void test_num_of_recipients_multiple_events_with_closing_dummy_event() throws Exception {
		/* ******************************************************************* event id = aaa ***************************************************************************/

        DgEventInput input1 = new DgEventInputBuilder()
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

        DgEventInput input2 = new DgEventInputBuilder()
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
                .setIsExternal(true)
                .setNumOfRecipients(1)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                // interesting test stuff starts here
                .setDestinationFile("message_body")
                .setEventType("message_body")
                .createDgEventAfterEtl();

        DgEventInput input3 = new DgEventInputBuilder()
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
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                // interesting test stuff starts here
                .setDestinationFile("")
                .setEventType("recipient")
                .createDgEventAfterEtl();



		/* ******************************************************************* event id = bbb ***************************************************************************/

        DgEventInput input4 = new DgEventInputBuilder()
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
                .setIsExternal(true)
                .setNumOfRecipients(2)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                // interesting test stuff starts here
                .setDestinationFile("message_body")
                .setEventType("message_body")
                .createDgEventAfterEtl();

        DgEventInput input5 = new DgEventInputBuilder()
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

        DgEventInput input6 = new DgEventInputBuilder()
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
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                // interesting test stuff starts here
                .setDestinationFile("")
                .setEventType("recipient")
                .createDgEventAfterEtl();

        DgEventInput input7 = new DgEventInputBuilder()
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
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                // interesting test stuff starts here
                .setDestinationFile("")
                .setEventType("recipient")
                .createDgEventAfterEtl();


/* ******************************************************************* event id = ccc ***************************************************************************/



        DgEventInput input8 = new DgEventInputBuilder()
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

        DgEventInput input9 = new DgEventInputBuilder()
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
                .setIsExternal(true)
                .setNumOfRecipients(0)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                // interesting test stuff starts here
                .setDestinationFile("")
                .setEventType("recipient")
                .createDgEventAfterEtl();

        DgEventInput input10 = new DgEventInputBuilder()
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
                .setIsExternal(true)
                .setNumOfRecipients(1)
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEmailRecipient("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the @ - this is ok for this test
                // interesting test stuff starts here
                .setDestinationFile("message_body")
                .setEventType("message_body")
                .createDgEventAfterEtl();

		/* ****************************************************************************************************************************************************/

		/* eventId = aaa */
        morphlineTester.testSingleLine("1", input1.toString(), expected1.toString()); //id aaa - attachment
        morphlineTester.testSingleLine("2", input2.toString(), null);  //id aaa - message_body
        morphlineTester.testSingleLine("3", input3.toString(), expected3.toString()); //id aaa - recipient

		/* eventId = bbb */
        morphlineTester.testSingleLine("4", input4.toString(), expected2.toString()); //id bbb - message_body
        morphlineTester.testSingleLine("5", input5.toString(), expected5.toString()); //id bbb - attachment
        morphlineTester.testSingleLine("6", input6.toString(), expected6.toString()); //id bbb - recipient
        morphlineTester.testSingleLine("7", input7.toString(), expected7.toString()); //id bbb - recipient

		/* eventId = ccc */
        morphlineTester.testSingleLine("8", input8.toString(), expected8.toString()); //id ccc - attachment
        morphlineTester.testSingleLine("10", input9.toString(), expected4.toString());//id ccc - recipient
        morphlineTester.testSingleLine("9", input10.toString(), expected9.toString());  //id ccc - message_body


        morphlineTester.testSingleLine("11", DUMMY_EVENT_STRING, expected10.toString()); //dummy


    }

}
