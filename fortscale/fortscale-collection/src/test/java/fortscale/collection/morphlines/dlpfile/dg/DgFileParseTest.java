package fortscale.collection.morphlines.dlpfile.dg;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.dlpmail.digitalguardian.DgEventInput;
import fortscale.collection.morphlines.dlpmail.digitalguardian.DgEventInputBuilder;
import fortscale.collection.morphlines.dlpmail.digitalguardian.DgMailEventAfterEtl;
import fortscale.collection.morphlines.dlpmail.digitalguardian.DgMailEventAfterEtlBuilder;
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
    @Ignore
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

    @Test
    public void test_parse_file() {
        String testCase = "Test the parsing (see if the fields are where we expect them)";
        final String inputLine = "2016.05.19,5/19/2016 12:57:26 PM,5/19/2016 10:57:26 AM,explorer.exe,workgroup\\vdely-ConfChecker,Windows,,,,,,,,B69554FF-91BE-48DF-B317-9B4D04731149,vdely-ConfChecker\\vincent,,microsoft,4583DAF9442880204730FB2C8A060430640494B1,6A671B92A69755DE6FD063FCBE4BA926D83B49F78C42DBAEED8CDB6BBC57576A,microsoft® windows® operating system,6.1.7601.17514,Scanned,12/09/2014 08:55,VirusTotal: 0 / 52 scans positive.,,,,,UNKNOWN_HOST,,,,A41BD201-AC2A-1037-5C25-806E2F378C60,,,ac4c51eb24aa95b77f705ab159189e24,eb514cac-aa24-b795-7f70-5ab159189e24,Inbound,File Copy,,0,,FALSE,FALSE,FALSE,FALSE,FALSE,TRUE,0,FALSE,0,FALSE,FALSE,FALSE,,,,,,,,,,,,,Not Blocked,8628,8628,c:\\program files\\dgagent\\verity\\kv\\_nti40\\bin\\,licensekey.dat,,dat,71E32251-1DB0-11E6-A42A-1040F39D1057,8628,FALSE,,,,,licensekey.dat,,c:\\program files\\dgagent\\verity\\k2\\_nti40\\bin\\,licensekey.dat,,dat,,FALSE,FALSE,FALSE,0,0,FALSE,FALSE,FALSE,Fixed,327ff9ad-5620-20c7-9a9e-992779c05a35,Fixed,327ff9ad-5620-20c7-9a9e-992779c05a35,,,,,,Unknown";

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-05-19 10:57:00")
                .setDateTimeUnix("1463655420")
                .setEventType("copy")
                .setEventId("A41BD201-AC2A-1037-5C25-806E2F378C60")
                .setUsername("vdely-ConfChecker\\vincent")
                .setHostname("workgroup\\vdely-ConfChecker")
                .setNormalizedSrcMachine("workgroup\\vdely-ConfChecker")
                .setApplication("explorer.exe")
                .setDestinationFile("licensekey.dat")
                .setDetailFileSize(8628l)
                .setDestinationDirectory("c:\\program files\\dgagent\\verity\\kv\\_nti40\\bin\\")
                .setWasClassified("FALSE")
                .setWasBlocked("FALSE")
                .setScanValueStatusText("VirusTotal: 0 / 52 scans positive.")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                .setFullName("")
                .setIpAddress("")
                .setSourceDirectory("c:\\program files\\dgagent\\verity\\k2\\_nti40\\bin\\")
                .setSourceFile("licensekey.dat")
                .setSourceDriveType("fixed")
                .setDestinationDriveType("fixed")
                .createDgEventAfterEtl();
        morphlineTester.testSingleLine(testCase, inputLine, expected.toString());
    }
}






