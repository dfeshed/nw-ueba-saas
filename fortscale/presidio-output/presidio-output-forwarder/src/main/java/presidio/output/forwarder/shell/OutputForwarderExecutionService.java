package presidio.output.forwarder.shell;

import fortscale.utils.logging.Logger;
import presidio.output.forwarder.services.PresidioOutputForwardService;

import java.time.Instant;

public class OutputForwarderExecutionService {
    private static final Logger logger = Logger.getLogger(OutputForwarderExecutionService.class);


    private PresidioOutputForwardService presidioOutputForwardService;

    public OutputForwarderExecutionService(PresidioOutputForwardService presidioOutputForwardService) {
        this.presidioOutputForwardService = presidioOutputForwardService;
    }

    public void run(Instant startDate, Instant endDate) throws Exception {
        presidioOutputForwardService.forward(startDate, endDate);
    }

    public void clean(Instant startTime, Instant endTime) {
        logger.info("Cleanup is not implemented for this service");
    }
}

