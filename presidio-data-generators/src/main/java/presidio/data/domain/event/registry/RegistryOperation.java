package presidio.data.domain.event.registry;

import presidio.data.domain.ProcessEntity;
import presidio.data.domain.RegistryEntry;
import presidio.data.domain.event.OperationType;

/**
 * Events generator domain, contains all fields for DLPFileOperation generator
 */
public class RegistryOperation {
    private ProcessEntity process;
    private RegistryEntry registryEntry;
    private OperationType operationType;

    public RegistryOperation(ProcessEntity process, RegistryEntry registryEntry, OperationType operationType) {
        this.process = process;
        this.registryEntry = registryEntry;
        this.operationType = operationType;
    }

    public ProcessEntity getProcess() {
        return process;
    }

    public void setProcess(ProcessEntity process) {
        this.process = process;
    }

    public RegistryEntry getRegistryEntry() {
        return registryEntry;
    }

    public void setRegistryEntry(RegistryEntry registryEntry) {
        this.registryEntry = registryEntry;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public String toString() {
        return "RegistryOperation{" +
                "process=" + process +
                ", registryEntry=" + registryEntry +
                ", operationType=" + operationType +
                '}';
    }
}
