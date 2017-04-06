package fortscale.collection.morphlines.dlp.dlpfile;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.dlp.dlpfile.dg.DgFileEventAfterEtl;
import fortscale.collection.morphlines.dlp.dlpfile.dg.DgFileEventAfterEtlBuilder;
import fortscale.collection.morphlines.dlp.dlpmail.digitalguardian.DgEventInput;
import fortscale.collection.morphlines.dlp.dlpmail.digitalguardian.DgEventInputBuilder;
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
public class DlpFileEnrichTest {

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
    public void test_normalized_src_machine_is_like_hostname() {
        String testCase = "Test that normalized src machine is set to be like hostname field";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("File Open")
                .setComputerName("example_hostname")
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setFullName("some_givenName some_surname")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setEventType("open")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                        // interesting test stuff starts here
                .setHostname("example_hostname") // just to be explicit
                .setNormalizedSrcMachine("example_hostname")
                .createDgEventAfterEtl();

        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_data_source() {
        String testCase = "Test that the data_source is dlpfile";
        DgEventInput input = new DgEventInputBuilder()
                .setAgentUtcTime("06/12/2016 16:04")
                .setOperation("File Open")
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setFullName("some_givenName some_surname")
                .setLastState("etl")
                .setEventType("open")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                        // interesting test stuff starts here
                .setDataSource("dlpfile")
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
                .setOperation("File Open")
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setDateTime("2016-06-12 16:04:00")
                .setDateTimeUnix("1465747440")
                .setFullName("some_givenName some_surname")
                .setEventType("open")
                .setDataSource("dlpfile")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
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
                .setOperation("File Open")
                        // interesting test stuff starts here
                .setAgentUtcTime("06/12/2016 16:04")
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setFullName("some_givenName some_surname")
                .setEventType("open")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                        // interesting test stuff starts here
                .setDateTimeUnix("1465747440")
                .setDateTime("2016-06-12 16:04:00")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_is_rdp_flag() {
        String testCase = "Test dates formatting";
        DgEventInput input = new DgEventInputBuilder()
                .setOperation("File Open")
                        // interesting test stuff starts here
                .setAgentUtcTime("06/12/2016 16:04")
                .setExecutingApplication("mstsc.exe")
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setFullName("some_givenName some_surname")
                .setEventType("open")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("true")
                .setIsRegistryChanged("false")
                .setExecutingApplication("mstsc.exe")
                        // interesting test stuff starts here
                .setDateTimeUnix("1465747440")
                .setDateTime("2016-06-12 16:04:00")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_is_admin_activity_flag() {
        String testCase = "Test dates formatting";
        DgEventInput input = new DgEventInputBuilder()
                .setOperation("File Open")
                        // interesting test stuff starts here
                .setAgentUtcTime("06/12/2016 16:04")
                .setExecutingApplication("powershell.exe")
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setFullName("some_givenName some_surname")
                .setEventType("open")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("true")
                .setIsRdp("false")
                .setIsRegistryChanged("false")
                .setExecutingApplication("powershell.exe")
                        // interesting test stuff starts here
                .setDateTimeUnix("1465747440")
                .setDateTime("2016-06-12 16:04:00")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_is_registry_changed_flag() {
        String testCase = "Test dates formatting";
        DgEventInput input = new DgEventInputBuilder()
                .setOperation("File Open")
                        // interesting test stuff starts here
                .setAgentUtcTime("06/12/2016 16:04")
                .setExecutingApplication("regedit.exe")
                .createDgEvent();

        DgFileEventAfterEtl expected = new DgFileEventAfterEtlBuilder()
                .setFullName("some_givenName some_surname")
                .setEventType("open")
                .setDataSource("dlpfile")
                .setLastState("etl")
                .setIsAdminActivity("false")
                .setIsRdp("false")
                .setIsRegistryChanged("true")
                .setExecutingApplication("regedit.exe")
                        // interesting test stuff starts here
                .setDateTimeUnix("1465747440")
                .setDateTime("2016-06-12 16:04:00")
                .createDgEventAfterEtl();


        final String inputLine = input.toString();
        final String expectedOutput = expected.toString();
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }
}
