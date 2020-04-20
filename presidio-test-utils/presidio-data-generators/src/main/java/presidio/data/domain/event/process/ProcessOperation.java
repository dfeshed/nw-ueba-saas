package presidio.data.domain.event.process;

import presidio.data.domain.ProcessEntity;
import presidio.data.domain.event.OperationType;

/**
 * Events generator domain, contains all fields for DLPFileOperation generator
 */
public class ProcessOperation {
    private ProcessEntity sourceProcess;
    private ProcessEntity destinationProcess;
    private OperationType operationType;

    public ProcessOperation(ProcessEntity sourceProcess, ProcessEntity destinationProcess, OperationType operationType) {
        this.sourceProcess = sourceProcess;
        this.destinationProcess = destinationProcess;
        this.operationType = operationType;
    }

    public ProcessEntity getSourceProcess() {
        return sourceProcess;
    }

    public void setSourceProcess(ProcessEntity sourceProcess) {
        this.sourceProcess = sourceProcess;
    }

    public ProcessEntity getDestinationProcess() {
        return destinationProcess;
    }

    public void setDestinationProcess(ProcessEntity destinationProcess) {
        this.destinationProcess = destinationProcess;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public String toString() {
        return "ProcessOperation{" +
                "sourceProcess=" + sourceProcess.toString() +
                ", destinationProcess=" + destinationProcess.toString() +
                ", operationType=" + operationType.toString() +
                '}';
    }
}
