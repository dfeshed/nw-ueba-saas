package presidio.output.forwarder.shell;

import fortscale.utils.logging.Logger;
import presidio.output.forwarder.services.OutputForwardService;

import java.time.Instant;

public class OutputForwarderExecutionService {
    private static final Logger logger = Logger.getLogger(OutputForwarderExecutionService.class);


    private OutputForwardService presidioOutputForwardService;

    public OutputForwarderExecutionService(OutputForwardService presidioOutputForwardService) {
        this.presidioOutputForwardService = presidioOutputForwardService;
    }

    public int doRun(Instant startDate, Instant endDate, String entityType) throws Exception {
        return presidioOutputForwardService.forward(startDate, endDate, entityType);

    }

    public int doClean(Instant startTime, Instant endTime) {

        logger.info("There is nothing to clean in this service");
        return 0;
    }
}

