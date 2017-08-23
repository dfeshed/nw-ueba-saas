package presidio.adapter.util;

import fortscale.common.general.Schema;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

@RunWith(value=RunOnlyOnLinux.class)
public class FlumeConfigurationUtilTest {

    public static final String mockedModuleName = "adapter";
    public static final String mockedSmartKit = "ca";
    private String mockedFlumeHome;
    private final FlumeConfigurationUtil testedFlumeConfigurationUtil = new FlumeConfigurationUtil(mockedModuleName, mockedSmartKit);

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
    private String mockedAfterTestsFilePath;

    @Before
    public void setUp() throws Exception {
        Path currentRelativePath = Paths.get("");
        String currentDirectory = currentRelativePath.toAbsolutePath().toString();
        mockedFlumeHome = currentDirectory + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator;
        environmentVariables.set("FLUME_HOME", mockedFlumeHome);

        String mockedConfFolder = mockedFlumeHome + "conf" + File.separator + "adapter" + File.separator;
        String mockedPropertiesFile = "active_directory.properties";
        mockedAfterTestsFilePath = mockedConfFolder + "after_util_test_" + mockedPropertiesFile;
        final Path pathToTestCreatedFile = Paths.get(mockedAfterTestsFilePath);
        try {
            Files.delete(pathToTestCreatedFile);
        } catch (IOException e) {
            //ignore
        }
        Files.createFile(pathToTestCreatedFile);
    }

    @After
    public void tearDown() throws Exception {
        try {
            Files.delete(Paths.get(mockedAfterTestsFilePath));

            final String flumeExecutionConfFile = "active_directory_1970-01-01T00:00:00Z---1971-01-01T00:00:00Z.properties";
            final String createdFile = mockedFlumeHome + "conf" + File.separator + mockedModuleName + File.separator + flumeExecutionConfFile;
            Files.delete(Paths.get(createdFile));
        } catch (IOException e) {
            //ignore
        }
    }

    @Test
    public void createExecutionConfFile() throws Exception {
        Instant startDate = Instant.EPOCH; //1970-01-01T00:00:00Z
        Instant endDate = startDate.plus((Duration.ofDays(365))); //1971-01-01T00:00:00Z
        final String flumeExecutionScriptPath = testedFlumeConfigurationUtil.createExecutionConfFile(Schema.ACTIVE_DIRECTORY, startDate, endDate);
        final String flumeExecutionConfFile = "active_directory_1970-01-01T00:00:00Z---1971-01-01T00:00:00Z.properties";
        final String expected = mockedFlumeHome + "conf" + File.separator + mockedModuleName + File.separator + flumeExecutionConfFile;
        Assert.assertEquals("Flume execution script path is invalid", expected, flumeExecutionScriptPath);

        FileInputStream in = new FileInputStream(expected);
        Properties props = new Properties();
        props.load(in);
        in.close();

        /* edit the properties */
        for (Object key : props.keySet()) {
            String currProperty = (String) key;
            switch (currProperty) {
                case "something.something.something.startDate":
                    final String startDateProperty = props.getProperty(currProperty);
                    Assert.assertEquals("startDate is invalid", "1970-01-01T00:00:00Z", startDateProperty);
                    break;
                case "something.something.something.endDate":
                    final String endDateProperty = props.getProperty(currProperty);
                    Assert.assertEquals("endDate is invalid", "1971-01-01T00:00:00Z", endDateProperty);
                    break;
                case "something.something.something.some_field_doesn't_matter":
                    final String some_field_doesnt_matterProperty = props.getProperty(currProperty);
                    Assert.assertEquals("endDate is invalid", "should stay the same", some_field_doesnt_matterProperty);
                    break;
            }
        }
    }

    @Test
    public void createSchemaPrefix() throws Exception {
        final String schemaPrefix = testedFlumeConfigurationUtil.createSchemaPrefix(Schema.ACTIVE_DIRECTORY);
        Assert.assertEquals("Schema prefix is invalid", "active_directory", schemaPrefix);
    }

    @Test
    public void getFlumeHome() throws Exception {
        final String flumeHome = testedFlumeConfigurationUtil.getFlumeHome();
        Assert.assertEquals("Conf folder path is invalid", mockedFlumeHome, flumeHome);
    }

    @Test
    public void createConfFolderPath() throws Exception {
        final String confFolderPath = testedFlumeConfigurationUtil.createConfFolderPath();
        final String expected = mockedFlumeHome + "conf" + File.separator ;
        Assert.assertEquals("Conf folder path is invalid", expected, confFolderPath);
    }

    @Test
    public void createConfFileName() throws Exception {
        final String confFileName = testedFlumeConfigurationUtil.createConfFileName(Schema.ACTIVE_DIRECTORY);
        final String expected = "active_directory.properties";
        Assert.assertEquals("Conf folder name is invalid", expected, confFileName);
    }

    @Test
    public void createAgentName() throws Exception {
        final String agentName = testedFlumeConfigurationUtil.createAgentName(Schema.ACTIVE_DIRECTORY);
        final String expected = "caActive_directoryAgent";
        Assert.assertEquals("Agent name is invalid", expected, agentName);
    }

    @Test
    public void getConfFilePathArgument() throws Exception {
        final String confFilePathArgument = testedFlumeConfigurationUtil.getConfFilePathArgument(Schema.ACTIVE_DIRECTORY);
        final String expected = "--conf-file active_directory.properties";
        Assert.assertEquals("Conf file path argument is invalid", expected, confFilePathArgument);
    }

    @Test
    public void getConfFolderArgument() throws Exception {
        final String confFolderPath = testedFlumeConfigurationUtil.getConfFolderArgument();
        final String expected = "--conf " + mockedFlumeHome + "conf" + File.separator;
        Assert.assertEquals("Conf folder argument is invalid", expected, confFolderPath);
    }

    @Test
    public void getExecuteAgentCommand() throws Exception {
        final String executeAgentCommand = testedFlumeConfigurationUtil.getExecuteAgentCommand();
        final String expected = "agent";
        Assert.assertEquals("execute agent command is invalid", expected, executeAgentCommand);
    }

    @Test
    public void getAgentNameArgument() throws Exception {
        final String agentNameArgument = testedFlumeConfigurationUtil.getAgentNameArgument(Schema.ACTIVE_DIRECTORY);
        final String expected = "--name caActive_directoryAgent";
        Assert.assertEquals("Agent name argument is invalid", expected, agentNameArgument);
    }

    @Test
    public void getFlumeExecutionScriptPath() throws Exception {
        final String flumeExecutionScriptPath = testedFlumeConfigurationUtil.getFlumeExecutionScriptPath();
        final String expected = mockedFlumeHome + FlumeConfigurationUtil.BIN_FLUME_NG_PATH;
        Assert.assertEquals("Flume execution script path is invalid", expected, flumeExecutionScriptPath);
    }

}