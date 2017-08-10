package presidio.input.core.services.impl;


import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.logging.Logger;
import presidio.input.core.services.data.AdeDataService;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;

public class InputExecutionServiceImpl implements PresidioExecutionService {

    private static final Logger logger = Logger.getLogger(InputExecutionServiceImpl.class);

    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final AdeDataService adeDataService;
    private final OutputDataServiceSDK outputDataServiceSDK;

    public InputExecutionServiceImpl(PresidioInputPersistencyService presidioInputPersistencyService,
                                     AdeDataService adeDataService,
                                     OutputDataServiceSDK outputDataServiceSDK) {
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.adeDataService = adeDataService;
        this.outputDataServiceSDK = outputDataServiceSDK;
    }

    @Override
    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        logger.info("Started input processing with params: data source:{}, from {}:{}, until {}:{}.", schema, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        InputCoreManager inputCoreManager = new InputCoreManager(presidioInputPersistencyService, adeDataService, outputDataServiceSDK);
        inputCoreManager.run(schema, startDate, endDate);
        logger.info("Finished input run with params : data source:{}, from {}:{}, until {}:{}.", schema, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
    }

    @Override
    public void cleanAll(Schema schema) throws Exception {
        logger.info("Started clean processing for data source:{}.", schema);
        presidioInputPersistencyService.cleanAll(schema);
    }

    @Override
    public void clean(Schema schema, Instant startDate, Instant endDate) throws Exception {
        logger.info("Started clean processing for data source:{}, from {}:{}, until {}:{}."
                , schema,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        presidioInputPersistencyService.clean(schema, startDate, endDate);
        logger.info("Finished enrich processing .");
    }
}
