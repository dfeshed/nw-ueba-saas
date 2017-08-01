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

    private final ProcessExecutor processExecutor;
    private final FlumeConfigurationUtil flumeConfigurationUtil;

    public FlumeAdapterExecutionService(ProcessExecutor processExecutor, FlumeConfigurationUtil flumeConfigurationUtil) {
        this.processExecutor = processExecutor;
        this.flumeConfigurationUtil = flumeConfigurationUtil;
    }

    @Override
    public void clean(Schema schema, Instant startDate, Instant endDate) throws Exception {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void cleanAll(Schema schema) throws Exception {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration, Double featureBucketStrategy) throws Exception {
        logger.info("Starting Adapter with params: schema: {}, {} : {}, {} : {}.",
                schema, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        String newFilePath = createExecutionConfFile(schema, startDate, endDate);
        logger.info("finish configuring adapter. Configuration file path: {}", newFilePath);

        String jobName = flumeConfigurationUtil.createJobName(schema, startDate, endDate);
        runAdapterInstance(schema, jobName, newFilePath);
        logger.info("Adapter for schema {] is now running.", schema);
    }

    private String createExecutionConfFile(Schema schema, Instant startDate, Instant endDate) throws IOException {
        /* load the properties */
        final String confFilePath = flumeConfigurationUtil.createConfFolderPath() + flumeConfigurationUtil.createConfFileName(schema);
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
        final String newFileName = flumeConfigurationUtil.createConfFolderPath() + flumeConfigurationUtil.createJobName(schema, startDate, endDate) + ".properties";
        FileOutputStream out = new FileOutputStream(newFileName);
        props.store(out, null);
        out.close();

        return newFileName;
    }


    private void runAdapterInstance(Schema schema, String jobName, String newFilePath) {
        final String flumeExecutionScriptPath = flumeConfigurationUtil.getFlumeExecutionScriptPath();
        final String executionArgument = flumeConfigurationUtil.getExecuteAgentCommand();
        final String agentNameArgument = flumeConfigurationUtil.getAgentNameArgument(schema);
        final String confFolderArgument = flumeConfigurationUtil.getConfFolderArgument();
        final String newFlumeExecutionConfFileArgument = flumeConfigurationUtil.getFlumeExecutionConfFileArgument(newFilePath);

        List<String> arguments = Arrays.asList(flumeExecutionScriptPath, executionArgument, agentNameArgument, confFolderArgument, newFlumeExecutionConfFileArgument);
        processExecutor.executeProcess(jobName, arguments, flumeConfigurationUtil.getFlumeHome());
    }
}

