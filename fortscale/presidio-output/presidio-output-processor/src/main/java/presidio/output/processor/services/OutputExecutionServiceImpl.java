package presidio.output.processor.services;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.DataSource;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.ade.sdk.executions.common.ADEManagerSDK;
import presidio.output.processor.services.alert.AlertService;

import java.time.Instant;

/**
 * Created by shays on 17/05/2017.
 * Main output functionality is implemented here
 */

public class OutputExecutionServiceImpl implements PresidioExecutionService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static int SMART_SCORE_THRESHOLD = 50;

    private final ADEManagerSDK adeManagerSDK;
    private final AlertService alertService;

    public OutputExecutionServiceImpl(ADEManagerSDK adeManagerSDK, AlertService alertService) {
        this.adeManagerSDK = adeManagerSDK;
        this.alertService = alertService;
    }

    /**
     * Run the output processor main functionality which consist of the following-
     * 1. Get SMARTs from ADE and create Alerts entities for SMARTs with score higher than the threshold
     * 2. Enrich alerts with information from Input component (fields which were not part of the ADE schema)
     * 3. Alerts classification (rule based semantics)
     * 4. Calculates supporting information
     * @param dataSource
     * @param startDate
     * @param endDate
     * @param fixedDuration
     * @throws Exception
     */
    @Override
    public void run(DataSource dataSource, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        logger.debug("Started output process with params: data source:{}, from {}:{}, until {}:{}.", dataSource, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        //1. Get SMARTs from ADE
        //TODO- change page size and score threshold (configurable)
        PageIterator<EntityEvent> smarts = adeManagerSDK.findSmarts(new TimeRange(startDate, endDate), 100, SMART_SCORE_THRESHOLD);
        alertService.generateAlerts(smarts);


    }

    @Override
    public void clean(DataSource dataSource, Instant startDate, Instant endDate) throws Exception {
        // TODO: Implement
    }

    @Override
    public void cleanAll(DataSource dataSource) throws Exception {
        // TODO: Implement
    }
}
