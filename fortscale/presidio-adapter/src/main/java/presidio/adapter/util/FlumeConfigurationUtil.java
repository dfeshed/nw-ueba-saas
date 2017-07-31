package presidio.adapter.util;

import fortscale.common.general.Schema;

import java.io.File;

public class FlumeConfigurationUtil {

    private static final String AGENT_NAME_FLAG = "--name";
    private static final String CONF_FOLDER_FLAG = "--conf";
    private static final String CONF_FILE_PATH_FLAG = "--conf-file";
    protected static final String BIN_FLUME_NG_PATH = "bin/flume-ng";
    private static final String EXECUTE_AGENT_COMMAND = "agent";

    /**
     * active Directory => active_directoryAgent
     *
     * @param schema
     */
    public static String createSchemaPrefix(Schema schema) { //active Directory => active_directory
        return schema.getName().toLowerCase().replace(" ", "_");
    }


    /**
     * FLUME_HOME/conf/
     */
    public static String createConfFolderPath() { //flume_home/conf/
        return getFlumeHome() + "conf" + File.separator;
    }

    public static String getFlumeHome() {
        final String flumeHome = System.getenv("FLUME_HOME");
        return flumeHome;
    }

    /**
     * active Directory => active_directory.properties
     *
     * @param schema
     */
    public static String createConfFileName(Schema schema) { //active Directory => active_directory.properties
        return createSchemaPrefix(schema) + ".properties";
    }

    /**
     * active Directory => active_directoryAgent
     *
     * @param schema
     */
    public static String createAgentName(Schema schema) { //active Directory => active_directoryAgent
        return createSchemaPrefix(schema) + "Agent";
    }


    /**
     * active Directory => --conf-file active_directory.properties
     *
     * @param schema
     */
    public static String getConfFilePathArgument(Schema schema) {
        return CONF_FILE_PATH_FLAG + " " + FlumeConfigurationUtil.createConfFileName(schema);
    }


    /**
     * -conf FLUME_HOME/conf/
     *
     * @return -conf FLUME_HOME/conf/
     */
    public static String getConfFolderArgument() {
        return CONF_FOLDER_FLAG + " " + FlumeConfigurationUtil.createConfFolderPath();
    }

    public static String getExecuteAgentCommand() {
        return EXECUTE_AGENT_COMMAND;
    }

    /**
     * active Directory => --name active_directoryAgent
     *
     * @param schema
     * @return
     */
    public static String getAgentNameArgument(Schema schema) {
        return AGENT_NAME_FLAG + " " + FlumeConfigurationUtil.createAgentName(schema);
    }

    /**
     * FLUME_HOME/bin/flume-ng
     */
    public static String getFlumeExecutionScriptPath() {
        return FlumeConfigurationUtil.getFlumeHome() + BIN_FLUME_NG_PATH;
    }

}
