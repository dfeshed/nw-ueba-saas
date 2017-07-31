package presidio.adapter.services.impl;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.adapter.util.FlumeConfigurationUtil;
import presidio.adapter.util.ProcessExecutor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class FlumeAdapterExecutionService implements PresidioExecutionService {
    private static Logger logger = LoggerFactory.getLogger(FlumeAdapterExecutionService.class);

    private static final String FLUME_CONF_START_DATE_FIELD_NAME = "startDate";
    private static final String FLUME_CONF_END_DATE_FIELD_NAME = "endDate";

    @Override
    public void clean(Schema schema, Instant startDate, Instant endDate) throws Exception {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void cleanAll(Schema schema) throws Exception {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        logger.info("Starting Adapter with params: schema: {}, {} : {}, {} : {}.",
                schema, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        String configurationPath = configureAdapterInstance(schema, startDate, endDate);
        logger.info("finish configuring adapter. Configuration file path: {}", configurationPath);

        String jobName = createJobName(schema, startDate, endDate);
        runAdapterInstance(schema, jobName);
        logger.info("Adapter for schema {] is now running.", schema);
    }

    private String configureAdapterInstance(Schema schema, Instant startDate, Instant endDate) throws IOException {
        /* load the properties */
        final String confFilePath = FlumeConfigurationUtil.createConfFolderPath() + FlumeConfigurationUtil.createConfFileName(schema);
        FileInputStream in = new FileInputStream(confFilePath);
        Properties props = new Properties();
        props.load(in);
        in.close();

        /* edit the properties */
        for (Object key : props.keySet()) {
            String currProperty = (String) key;
            if (currProperty.endsWith(FLUME_CONF_START_DATE_FIELD_NAME)) {
                props.setProperty(currProperty, startDate.toString());
            } else if (currProperty.endsWith(FLUME_CONF_END_DATE_FIELD_NAME)) {
                props.setProperty(currProperty, endDate.toString());
            }
        }

        /* save the properties */
        FileOutputStream out = new FileOutputStream(createJobName(schema, startDate, endDate) + ".properties");
        props.store(out, null);
        out.close();

        return confFilePath;
    }


    private void runAdapterInstance(Schema schema, String jobName) {
        final String flumeExecutionScriptPath = FlumeConfigurationUtil.getFlumeExecutionScriptPath();
        final String executionArgument = FlumeConfigurationUtil.getExecuteAgentCommand();
        final String agentNameArgument = FlumeConfigurationUtil.getAgentNameArgument(schema);
        final String confFolderArgument = FlumeConfigurationUtil.getConfFolderArgument();
        final String confFilePathArgument = FlumeConfigurationUtil.getConfFilePathArgument(schema);

        List<String> arguments = Arrays.asList(flumeExecutionScriptPath, executionArgument, agentNameArgument, confFolderArgument, confFilePathArgument);
        ProcessExecutor.executeProcess(jobName, arguments, FlumeConfigurationUtil.getFlumeHome());
    }


    /**
     * datasource_sd---ed
     *
     * @param schema
     * @param startDate
     * @param endDate
     * @return datasource_sd---ed
     */
    private String createJobName(Schema schema, Instant startDate, Instant endDate) { //datasource_sd---ed
        return FlumeConfigurationUtil.createSchemaPrefix(schema) + "_" + startDate.toString() + "---" + endDate.toString();
    }


}

