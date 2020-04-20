package presidio.data.generators.event.process;

import presidio.data.domain.event.process.ProcessEvent;

public class ProcessDescriptionGenerator implements IProcessDescriptionGenerator{

    private String buildProcessDescription(ProcessEvent processEvent){
        String operationType = processEvent.getProcessOperation().getOperationType().getName();
        String description;
        String op = operationType.substring(operationType.indexOf("_") + 1, operationType.length()).toLowerCase();
        op = op.replaceAll("_", " ");

        description = "The source process  " + processEvent.getProcessOperation().getSourceProcess().getProcessFileName() +
                    " " + op + " destination process " + processEvent.getProcessOperation().getDestinationProcess().getProcessFileName();

        return description;
    }

    @Override
    public void updateProcessDescription(ProcessEvent processEvent) {
        processEvent.setProcessEventDescription(buildProcessDescription(processEvent));
    }
}
