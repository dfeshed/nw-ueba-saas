package fortscale.collection.morphlines.dlpmail;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.dlpmail.digitalguardian.DgMailEventAfterEtl;
import fortscale.collection.morphlines.dlpmail.digitalguardian.DgMailEventAfterEtlBuilder;
import fortscale.collection.morphlines.dlpmail.digitalguardian.DgMailEventInput;
import fortscale.collection.morphlines.dlpmail.digitalguardian.DgEventInputBuilder;
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
public class DlpMailEnrichTest {

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
    public void test_normalized_src_machine_is_like_hostname() {
        String testCase = "Test that normalized src machine is set to be like hostname field";
        DgMailEventInput input = new DgEventInputBuilder()
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
                .setIsExternal("true")
                .setNumOfRecipients("0")
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
        DgMailEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("Send Mail")
                .setDestinationFileExtension("jpeg")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setIsExternal("true")
                .setNumOfRecipients("0")
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
        DgMailEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("Send Mail")
                .setDestinationFileExtension("wdb")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setIsExternal("true")
                .setNumOfRecipients("0")
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
        DgMailEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("Send Mail")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setIsExternal("true")
                .setNumOfRecipients("0")
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
        DgMailEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("Send Mail")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setIsExternal("true")
                .setNumOfRecipients("0")
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
        DgMailEventInput input = new DgEventInputBuilder()
                .setOperation("Send Mail")
                // interesting test stuff starts here
                .setAgentUtcTime("06/12/2016 16:04")
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setEventType("attachment")
                .setIsAttachmentExtensionBlacklisted("false")
                .setIsExternal("true")
                .setNumOfRecipients("0")
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
        DgMailEventInput input = new DgEventInputBuilder()
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
                .setIsExternal("true")
                .setNumOfRecipients("0")
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
    @Ignore /* we are currently filtering "Attach Mail" events because we only support DG events (when it comes to parsing) */
    public void test_set_event_type_attachment() {
        String testCase = "Test that event type is attachment for events that are \"Attach Mail\" OR \"Send Mail\" with (attachment_file_name!=\"\" | \"message body\"";
        DgMailEventInput input = new DgEventInputBuilder()
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
                .setIsExternal("true")
                .setNumOfRecipients("0")
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
    }

    @Test
    public void test_num_of_recipients() { //TODO: update this when num of recipients calculation is finished
        String testCase = "Test  num_of_recipients is calculated correctly";
        DgMailEventInput input = new DgEventInputBuilder()
                .setOperation("Send Mail")
                .setAgentUtcTime("06/12/2016 16:04")
                // interesting test stuff starts here
                .createDgEvent();

        DgMailEventAfterEtl expected = new DgMailEventAfterEtlBuilder()
                .setDateTimeUnix("1465747440")
                .setDateTime("2016-06-12 16:04:00")
                .setEventDescription("Send Mail")
                .setFullName("some_givenName some_surname")
                .setIsAttachmentExtensionBlacklisted("false")
                .setIsExternal("true")
                .setDataSource("dlpmail")
                .setLastState("etl")
                .setEventType("attachment")
                .setEmailRecipientDomain("some_emailRecipient") // because the parsing wont find the domain - this is ok for this test
                // interesting test stuff starts here
                .setNumOfRecipients("0")
                .createDgEventAfterEtl();

        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

}
