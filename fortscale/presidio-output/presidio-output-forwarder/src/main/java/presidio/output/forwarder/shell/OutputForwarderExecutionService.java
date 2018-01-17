package presidio.output.forwarder.shell;

import presidio.output.forwarder.services.PresidioOutputForwardService;

import java.time.Instant;

public class OutputForwarderExecutionService {

    private PresidioOutputForwardService presidioOutputForwardService;

    public OutputForwarderExecutionService(PresidioOutputForwardService presidioOutputForwardService) {
        this.presidioOutputForwardService = presidioOutputForwardService;
    }

    public void run(Instant startDate, Instant endDate) throws Exception {
        presidioOutputForwardService.forward(startDate, endDate);
    }

//    public void clean(Instant startDate, Instant endDate) throws Exception {
//    }
//
//    public void cleanup(Instant startDate, Instant endDate, Double accumulationStrategy) throws Exception {
//    }
}

