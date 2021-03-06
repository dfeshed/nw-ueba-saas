package presidio.adapter.util;

import fortscale.common.general.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

/**
 * This class is a util class for handling and creating Flume configuration files
 * <p>
 * not static so it could be mocked
 */
public class FlumeConfigurationUtil {

    private static final String FLUME_CONF_START_DATE_FIELD_NAME = "startDate";
    private static final String FLUME_CONF_END_DATE_FIELD_NAME = "endDate";
    private static final String FLUME_CONF_BUCKET_FIELD_NAME = "bucket";
    private static final String FLUME_CONF_TENANT_FIELD_NAME = "tenant";
    private static final String FLUME_CONF_ACCOUNT_FIELD_NAME = "account";
    private static final String FLUME_CONF_REGION_FIELD_NAME = "region";
    private static final String AGENT_NAME_FLAG = "--name";
    private static final String CONF_FOLDER_FLAG = "--conf";
    protected static final String CONF_FILE_PATH_FLAG = "--conf-file";
    protected static final String BIN_FLUME_NG_PATH = "bin" + File.separator + "flume-ng";
    private static final String EXECUTE_AGENT_COMMAND = "agent";
    private final String moduleName;
    private final String smartKit;

    private String bucketName;
    private String tenant;
    private String account;
    private String region;

    public FlumeConfigurationUtil(String moduleName, String smartKit, String bucketName, String tenant, String account, String region) {
        this.bucketName = bucketName;
        this.tenant = tenant;
        this.account = account;
        this.region = region;
        this.moduleName = moduleName;
        this.smartKit = smartKit;
    }

    public String createExecutionConfFile(Schema schema, Instant startDate, Instant endDate) throws IOException {
        Properties props = new OrderedProperties<>(String.class);
        FileInputStream in = null;
        FileOutputStream out = null;

        /* load the properties */
        final String moduleConfFolder = createConfFolderPath() + moduleName + File.separator;
        try {
            final String confFilePath = moduleConfFolder + createConfFileName(schema);
            in = new FileInputStream(confFilePath);
            props.load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }

        /* edit the properties */
        for (Object key : props.keySet()) {
            String currProperty = (String) key;
            if (currProperty.endsWith(FLUME_CONF_START_DATE_FIELD_NAME)) {
                props.setProperty(currProperty, startDate.toString());
            } else if (currProperty.endsWith(FLUME_CONF_END_DATE_FIELD_NAME)) {
                props.setProperty(currProperty, endDate.toString());
            } else if (currProperty.endsWith(FLUME_CONF_BUCKET_FIELD_NAME)) {
                props.setProperty(currProperty, bucketName);
            } else if (currProperty.endsWith(FLUME_CONF_TENANT_FIELD_NAME)) {
                props.setProperty(currProperty, tenant);
            } else if (currProperty.endsWith(FLUME_CONF_ACCOUNT_FIELD_NAME)) {
                props.setProperty(currProperty, String.valueOf(account));
            } else if (currProperty.endsWith(FLUME_CONF_REGION_FIELD_NAME)) {
                props.setProperty(currProperty, region);
            }
        }

        /* save the properties */
        try {
            final String newFileName = moduleConfFolder + createJobName(schema, startDate, endDate) + ".properties";
            out = new FileOutputStream(newFileName);
            props.store(out, null);
            return newFileName;
        } finally {
            if (out != null) {
                out.close();
            }
        }
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
     * FLUME_HOME/conf/module_name/
     */
    public String createConfFolderPath() { //flume_home/conf/
        return getFlumeHome() + "conf" + File.separator;
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
        final String schemaPrefix = createSchemaPrefix(schema);
        String agentSchemaPrefix = Character.toUpperCase(schemaPrefix.charAt(0)) + schemaPrefix.substring(1);
        return smartKit + agentSchemaPrefix + "Agent";
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

    /**
     * This class is a {@link Properties} implementation that reads and writes in a sorted manner (according to the <i>natural ordering</i> of its
     * elements).
     */
    @SuppressWarnings("unused")
    private static class OrderedProperties<T extends Comparable> extends Properties {

        private final Class<T> keyType; //in order to keep the key type a comparable (POLA principle)

        private OrderedProperties(Class<T> keyType) {
            this.keyType = keyType;
        }

        @Override
        @SuppressWarnings("NullableProblems") //due to intellij bug
        public Set<Object> keySet() {
            return Collections.unmodifiableSet(new TreeSet<>(super.keySet()));
        }

        @Override
        public synchronized Enumeration<Object> keys() {
            return Collections.enumeration(new TreeSet<>(super.keySet()));
        }
    }



}




