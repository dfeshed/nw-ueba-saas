package presidio.data.generators.registryop;

import presidio.data.domain.event.registry.RegistryOperation;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IOperationTypeGenerator;
import presidio.data.generators.event.registry.CyclicOperationTypeGenerator;
import presidio.data.generators.processentity.IProcessEntityGenerator;
import presidio.data.generators.processentity.WindowsProcessEntityGenerator;
import presidio.data.generators.registryentry.IRegistryEntryGenerator;
import presidio.data.generators.registryentry.RegistryEntryGenerator;

public class RegistryOperationGenerator implements IRegistryOperationGenerator {
    private IProcessEntityGenerator processEntityGenerator;
    private IRegistryEntryGenerator registryEntryGenerator;
    private IOperationTypeGenerator operationTypeGenerator;

    public RegistryOperationGenerator(){
        processEntityGenerator = new WindowsProcessEntityGenerator();
        registryEntryGenerator = new RegistryEntryGenerator();
        operationTypeGenerator = new CyclicOperationTypeGenerator();
    }

    public RegistryOperation getNext(){
        return new RegistryOperation(processEntityGenerator.getNext(), registryEntryGenerator.getNext(), operationTypeGenerator.getNext());
    }

    public IProcessEntityGenerator getProcessEntityGenerator() {
        return processEntityGenerator;
    }

    public void setProcessEntityGenerator(IProcessEntityGenerator processEntityGenerator) {
        this.processEntityGenerator = processEntityGenerator;
    }

    public IRegistryEntryGenerator getRegistryEntryGenerator() {
        return registryEntryGenerator;
    }

    public void setRegistryEntryGenerator(IRegistryEntryGenerator registryEntryGenerator) {
        this.registryEntryGenerator = registryEntryGenerator;
    }

    public IOperationTypeGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(IOperationTypeGenerator operationTypeGenerator) {
        this.operationTypeGenerator = operationTypeGenerator;
    }
}
