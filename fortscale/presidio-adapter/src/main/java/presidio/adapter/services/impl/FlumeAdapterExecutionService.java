package presidio.adapter.services.impl;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.adapter.util.FlumeConfigurationUtil;
import presidio.adapter.util.ProcessExecutor;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;


public class FlumeAdapterExecutionService implements PresidioExecutionService {
    private static Logger logger = LoggerFactory.getLogger(FlumeAdapterExecutionService.class);

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
    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        logger.info("Starting Adapter with params: schema: {}, {} : {}, {} : {}.",
                schema, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        String newFilePath = flumeConfigurationUtil.createExecutionConfFile(schema, startDate, endDate);
        logger.info("finish configuring adapter. Configuration file path: {}", newFilePath);

        String jobName = flumeConfigurationUtil.createJobName(schema, startDate, endDate);
        runAdapterInstance(schema, jobName, newFilePath);
        logger.info("Adapter for schema {] is now running.", schema);
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

