package presidio.adapter.util;

import fortscale.common.general.Schema;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.File;

public class FlumeConfigurationUtilTest {

    public static final String SOME_FLUME_HOME_PATH = "/some_flume_home/";

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Before
    public void setUp() throws Exception {
        environmentVariables.set("FLUME_HOME", SOME_FLUME_HOME_PATH);
    }

    @Test
    public void createSchemaPrefix() throws Exception {
        final String schemaPrefix = FlumeConfigurationUtil.createSchemaPrefix(Schema.ACTIVE_DIRECTORY);
        Assert.assertEquals("Schema prefix is invalid", "active_directory", schemaPrefix);
    }

    @Test
    public void getFlumeHome() throws Exception {
        final String flumeHome = FlumeConfigurationUtil.getFlumeHome();
        Assert.assertEquals("Conf folder path is invalid", SOME_FLUME_HOME_PATH, flumeHome);
    }

    @Test
    public void createConfFolderPath() throws Exception {
        final String confFolderPath = FlumeConfigurationUtil.createConfFolderPath();
        final String expected = SOME_FLUME_HOME_PATH + "conf" + File.separator;
        Assert.assertEquals("Conf folder path is invalid", expected, confFolderPath);
    }

    @Test
    public void createConfFileName() throws Exception {
        final String confFileName = FlumeConfigurationUtil.createConfFileName(Schema.ACTIVE_DIRECTORY);
        final String expected = "active_directory.properties";
        Assert.assertEquals("Conf folder name is invalid", expected, confFileName);
    }

    @Test
    public void createAgentName() throws Exception {
        final String agentName = FlumeConfigurationUtil.createAgentName(Schema.ACTIVE_DIRECTORY);
        final String expected = "active_directoryAgent";
        Assert.assertEquals("Agent name is invalid", expected, agentName);
    }

    @Test
    public void getConfFilePathArgument() throws Exception {
        final String confFilePathArgument = FlumeConfigurationUtil.getConfFilePathArgument(Schema.ACTIVE_DIRECTORY);
        final String expected = "--conf-file active_directory.properties";
        Assert.assertEquals("Conf file path argument is invalid", expected, confFilePathArgument);
    }

    @Test
    public void getConfFolderArgument() throws Exception {
        final String confFolderPath = FlumeConfigurationUtil.getConfFolderArgument();
        final String expected = "--conf " + SOME_FLUME_HOME_PATH + "conf" + File.separator;
        Assert.assertEquals("Conf folder argument is invalid", expected, confFolderPath);
    }

    @Test
    public void getExecuteAgentCommand() throws Exception {
        final String executeAgentCommand = FlumeConfigurationUtil.getExecuteAgentCommand();
        final String expected = "agent";
        Assert.assertEquals("execute agent command is invalid", expected, executeAgentCommand);
    }

    @Test
    public void getAgentNameArgument() throws Exception {
        final String agentNameArgument = FlumeConfigurationUtil.getAgentNameArgument(Schema.ACTIVE_DIRECTORY);
        final String expected = "--name active_directoryAgent";
        Assert.assertEquals("Agent name argument is invalid", expected, agentNameArgument);
    }

    @Test
    public void getFlumeExecutionScriptPath() throws Exception {
        final String flumeExecutionScriptPath = FlumeConfigurationUtil.getFlumeExecutionScriptPath();
        final String expected = SOME_FLUME_HOME_PATH + FlumeConfigurationUtil.BIN_FLUME_NG_PATH;
        Assert.assertEquals("Flume execution script path is invalid", expected, flumeExecutionScriptPath);
    }

}