package presidio.adapter.services.impl;

import com.google.common.collect.ImmutableList;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.logging.Logger;
import presidio.adapter.util.FlumeConfigurationUtil;
import presidio.adapter.util.ProcessExecutor;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.io.IOException;
import java.time.Instant;

public class FlumeAdapterExecutionService implements PresidioExecutionService {
    private static Logger logger = Logger.getLogger(FlumeAdapterExecutionService.class);

    private final ProcessExecutor processExecutor;
    private final FlumeConfigurationUtil flumeConfigurationUtil;
    private final PresidioInputPersistencyService presidioInputPersistencyService;

    public FlumeAdapterExecutionService(ProcessExecutor processExecutor, FlumeConfigurationUtil flumeConfigurationUtil, PresidioInputPersistencyService presidioInputPersistencyService) {
        this.processExecutor = processExecutor;
        this.flumeConfigurationUtil = flumeConfigurationUtil;
        this.presidioInputPersistencyService = presidioInputPersistencyService;
    }

    @Override
    public void retentionClean(Schema schema, Instant startDate, Instant endDate) throws Exception {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void cleanAll(Schema schema) throws Exception {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        logger.info("Adapter is cleaning its output for schema {}, startDate {}, endDate {}", schema, startDate, endDate);
        presidioInputPersistencyService.clean(schema, startDate, endDate);
    }


    @Override
    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        logger.info("Starting Adapter with params: schema: {}, {} : {}, {} : {}.", schema, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        try {
            String newFilePath = configureNewFlumeExecution(schema, startDate, endDate);
            runFlumeExecution(schema, startDate, endDate, newFilePath);
        } catch (IOException e) {
            logger.error("Presidio Adapter run failed (with params: schema:{}, startDate:{}, endDate:{}, fixedDuration:{}).", schema, startDate, endDate, fixedDuration, e);
            throw e;
        }
    }

    private void runFlumeExecution(Schema schema, Instant startDate, Instant endDate, String newFilePath) {
        String jobName = flumeConfigurationUtil.createJobName(schema, startDate, endDate);
        runAdapterInstance(schema, jobName, newFilePath);
        logger.info("Adapter for schema[{}] with conf file[{}] is now running.", schema, newFilePath);
    }

    private String configureNewFlumeExecution(Schema schema, Instant startDate, Instant endDate) throws IOException {
        String newFilePath = flumeConfigurationUtil.createExecutionConfFile(schema, startDate, endDate);
        logger.info("finished configuring adapter. Configuration file path: {}", newFilePath);
        return newFilePath;
    }


    private void runAdapterInstance(Schema schema, String jobName, String newFilePath) {
        final String flumeExecutionScriptPath = flumeConfigurationUtil.getFlumeExecutionScriptPath();
        final String executionArgument = flumeConfigurationUtil.getExecuteAgentCommand();
        final String agentNameArgument = flumeConfigurationUtil.getAgentNameArgument(schema);
        final String confFolderArgument = flumeConfigurationUtil.getConfFolderArgument();
        final String newFlumeExecutionConfFileArgument = flumeConfigurationUtil.getFlumeExecutionConfFileArgument(newFilePath);

        final String[] confFolderArgumentSplit = confFolderArgument.split(" ");
        final String confFlag = confFolderArgumentSplit[0];
        final String confFlagValue = confFolderArgumentSplit[1];

        final String[] newFlumeExecutionConfFileArgumentSplit = newFlumeExecutionConfFileArgument.split(" ");
        final String confFileFlag = newFlumeExecutionConfFileArgumentSplit[0];
        final String confFileFlagValue = newFlumeExecutionConfFileArgumentSplit[1];

        final ImmutableList<String> args = ImmutableList.of(
                flumeExecutionScriptPath,
                executionArgument, agentNameArgument,
                confFlag, confFlagValue,
                confFileFlag,
                confFileFlagValue);

        processExecutor.executeProcess(jobName, args, flumeConfigurationUtil.getFlumeHome());
    }
}

