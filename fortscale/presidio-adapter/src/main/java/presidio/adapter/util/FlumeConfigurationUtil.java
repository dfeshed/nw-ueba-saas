package presidio.adapter.util;

import fortscale.common.general.Schema;

import java.io.File;
import java.time.Instant;

/**
 * This class is a util class for handling and creating Flume configuration files
 * <p>
 * not static so it could be mocked
 */
public class FlumeConfigurationUtil {

    private static final String AGENT_NAME_FLAG = "--name";
    private static final String CONF_FOLDER_FLAG = "--conf";
    protected static final String CONF_FILE_PATH_FLAG = "--conf-file";
    protected static final String BIN_FLUME_NG_PATH = "bin/flume-ng";
    private static final String EXECUTE_AGENT_COMMAND = "agent";
    private final String moduleName;

    public FlumeConfigurationUtil(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * active Directory => active_directoryAgent
     *
     * @param schema
     */
    public String createSchemaPrefix(Schema schema) { //active Directory => active_directory
        return schema.getName().toLowerCase().replace(" ", "_");
    }


    /**
     * FLUME_HOME/conf/
     */
    public String createConfFolderPath() { //flume_home/conf/
        return getFlumeHome() + "conf" + File.separator + moduleName + File.separator;
    }

    public String getFlumeHome() {
        final String flumeHome = System.getenv("FLUME_HOME");
        return flumeHome;
    }

    /**
     * active Directory => active_directory.properties
     *
     * @param schema
     */
    public String createConfFileName(Schema schema) { //active Directory => active_directory.properties
        return createSchemaPrefix(schema) + ".properties";
    }

    /**
     * active Directory => active_directoryAgent
     *
     * @param schema
     */
    public String createAgentName(Schema schema) { //active Directory => active_directoryAgent
        return createSchemaPrefix(schema) + "Agent";
    }


    /**
     * active Directory => --conf-file active_directory.properties
     *
     * @param schema
     */
    public String getConfFilePathArgument(Schema schema) {
        return CONF_FILE_PATH_FLAG + " " + createConfFileName(schema);
    }


    /**
     * --conf FLUME_HOME/conf/
     *
     * @return --conf FLUME_HOME/conf/
     */
    public String getConfFolderArgument() {
        return CONF_FOLDER_FLAG + " " + createConfFolderPath();
    }


    /**
     * @return "agent"
     */
    public String getExecuteAgentCommand() {
        return EXECUTE_AGENT_COMMAND;
    }

    /**
     * active Directory => --name active_directoryAgent
     *
     * @param schema
     * @return
     */
    public String getAgentNameArgument(Schema schema) {
        return AGENT_NAME_FLAG + " " + createAgentName(schema);
    }

    /**
     * FLUME_HOME/bin/flume-ng
     */
    public String getFlumeExecutionScriptPath() {
        return getFlumeHome() + BIN_FLUME_NG_PATH;
    }

    /**
     * datasource_sd---ed
     *
     * @param schema
     * @param startDate
     * @param endDate
     * @return datasource_sd---ed
     */
    public String createJobName(Schema schema, Instant startDate, Instant endDate) { //datasource_sd---ed
        return createSchemaPrefix(schema) + "_" + startDate.toString() + "---" + endDate.toString();
    }

    public String getFlumeExecutionConfFileArgument(String flumeExecutionConfFilePath) {
        return CONF_FILE_PATH_FLAG + " " + flumeExecutionConfFilePath;
    }
}
