package presidio.data.generators.event.registry;

import presidio.data.domain.ProcessEntity;
import presidio.data.domain.RegistryEntry;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.registry.RegistryOperation;
import presidio.data.generators.common.IOperationTypeGenerator;
import presidio.data.generators.processentity.IProcessEntityGenerator;
import presidio.data.generators.registryentry.IRegistryEntryGenerator;
import presidio.data.generators.registryop.IRegistryOperationGenerator;

public class ProcessRegistryOperationGenerator implements IRegistryOperationGenerator {
    private IProcessEntityGenerator processEntityGenerator;
    private ProcessRegistryEntryGenerator registryEntryGenerator;
    private IOperationTypeGenerator operationTypeGenerator;

    public ProcessRegistryOperationGenerator(IProcessEntityGenerator processEntityGenerator,
                                             ProcessRegistryEntryGenerator registryEntryGenerator,
                                             IOperationTypeGenerator operationTypeGenerator){
        this.processEntityGenerator = processEntityGenerator;
        this.registryEntryGenerator = registryEntryGenerator;
        this.operationTypeGenerator = operationTypeGenerator;
    }

    public RegistryOperation getNext(){
        ProcessEntity processEntity = processEntityGenerator.getNext();
        ProcessRegistryEntryGenerator.curProcess = processEntity;
        RegistryEntry registryEntry = registryEntryGenerator.getNext();
        OperationType operationType = operationTypeGenerator.getNext();
        return new RegistryOperation(processEntity, registryEntry, operationType);
    }

    public IProcessEntityGenerator getProcessEntityGenerator() {
        return processEntityGenerator;
    }

    public void setProcessEntityGenerator(IProcessEntityGenerator processEntityGenerator) {
        this.processEntityGenerator = processEntityGenerator;
    }

    public ProcessRegistryEntryGenerator getRegistryEntryGenerator() {
        return registryEntryGenerator;
    }

    public void setRegistryEntryGenerator(ProcessRegistryEntryGenerator registryEntryGenerator) {
        this.registryEntryGenerator = registryEntryGenerator;
    }

    public IOperationTypeGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(IOperationTypeGenerator operationTypeGenerator) {
        this.operationTypeGenerator = operationTypeGenerator;
    }

}
