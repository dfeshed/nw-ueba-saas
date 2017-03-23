package fortscale.collection.morphlines.dlpfile.dg;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.dlpmail.digitalguardian.DgEventInput;
import fortscale.collection.morphlines.dlpmail.digitalguardian.DgEventInputBuilder;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context-test-light.xml"})
public class DgFileParseTest {


    private MorphlinesTester morphlineTester = new MorphlinesTester();

    @Before
    public void setUp() throws Exception {

        PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-collection-test.properties");
        String kafkaMessageFields = propertiesResolver.getProperty("kafka.dlpfile.message.record.fields");

        List<String> outputFields = ImpalaParser.getTableFieldNames(kafkaMessageFields);
        String confFile = "resources/conf-files/parseDGFile.conf";
        String confEnrichmentFile = "resources/conf-files/enrichment/readDlpFile_enrich.conf";
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
    public void test_not_file_event_filter() {
        String testCase = "Test filter empty events";
        DgEventInput input = new DgEventInputBuilder().createDgEvent(); // event with all fields
        // interesting test stuff starts here
        input.operation = "Mail"; //we change the event operation

        final String inputLine = input.toString();
        morphlineTester.testSingleLineFiltered(testCase, inputLine);
    }

    @Test
    public void test_file_copy_without_dst_file_filter() {
        String testCase = "Test filter empty events";
        DgEventInput input = new DgEventInputBuilder().createDgEvent(); // event with all fields
        // interesting test stuff starts here
        input.operation = "File Copy"; //we change the event operation
        input.destinationFile = "";

        final String inputLine = input.toString();
        morphlineTester.testSingleLineFiltered(testCase, inputLine);
    }

    @Test
    public void test_file_copy_without_src_file_filter() {
        String testCase = "Test filter empty events";
        DgEventInput input = new DgEventInputBuilder().createDgEvent(); // event with all fields
        // interesting test stuff starts here
        input.operation = "File Copy"; //we change the event operation
        input.sourceFile = "";

        final String inputLine = input.toString();
        morphlineTester.testSingleLineFiltered(testCase, inputLine);
    }

    @Test
    public void test_remove_verdasys_prefix() {
        String testCase = "Test that the verdasys\r prefix is removed";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("File Open")
                        // interesting test stuff starts here
                .setComputerName("verdasys\rexample_hostname")
                .setUsername("verdasys\rexample_username")
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setFullName("some_givenName some_surname")
                .setEventType("open")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
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
    public void test_remove_extract_full_name() {
        String testCase = "Test that full_name is extracted from first_name and surname";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("File Move")
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setEventType("move")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                        // interesting test stuff starts here
                .setFullName("some_givenName some_surname")
                .createDgEventAfterEtl();

        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_move_event_mapping() {
        String testCase = "Test that the File Move event gets event type move";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("File Move")// interesting test stuff starts here
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setFullName("some_givenName some_surname")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                        // interesting test stuff starts here
                .setEventType("move")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_open_event_mapping() {
        String testCase = "Test that the File Open event gets event type move";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("File Open")// interesting test stuff starts here
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setFullName("some_givenName some_surname")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                        // interesting test stuff starts here
                .setEventType("open")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_delete_event_mapping() {
        String testCase = "Test that the File Open event gets event type move";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("File Delete")// interesting test stuff starts here
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setFullName("some_givenName some_surname")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                        // interesting test stuff starts here
                .setEventType("delete")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_recycle_event_mapping() {
        String testCase = "Test that the File Recycle event gets event type move";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("File Recycle")// interesting test stuff starts here
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setFullName("some_givenName some_surname")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                        // interesting test stuff starts here
                .setEventType("recycle")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_copy_event_mapping() {
        String testCase = "Test that the File Copy event gets event type move";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("File Copy")// interesting test stuff starts here
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setFullName("some_givenName some_surname")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                        // interesting test stuff starts here
                .setEventType("copy")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }
}






