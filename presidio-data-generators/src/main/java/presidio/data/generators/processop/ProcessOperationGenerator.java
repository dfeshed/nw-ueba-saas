package presidio.data.generators.processop;

import presidio.data.domain.event.process.ProcessOperation;
import presidio.data.generators.event.process.CyclicOperationTypeGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IOperationTypeGenerator;
import presidio.data.generators.processentity.IProcessEntityGenerator;
import presidio.data.generators.processentity.WindowsProcessEntityGenerator;

public class ProcessOperationGenerator implements IProcessOperationGenerator {
    private IProcessEntityGenerator sourceProcessEntityGenerator;
    private IProcessEntityGenerator destProcessEntityGenerator;
    private IOperationTypeGenerator operationTypeGenerator;

    public ProcessOperationGenerator() throws GeneratorException {
        sourceProcessEntityGenerator = new WindowsProcessEntityGenerator();
        destProcessEntityGenerator = new WindowsProcessEntityGenerator();
        operationTypeGenerator = new CyclicOperationTypeGenerator();
    }

    public ProcessOperation getNext(){
        return new ProcessOperation(getSourceProcessEntityGenerator().getNext(), getDestProcessEntityGenerator().getNext(),
                getOperationTypeGenerator().getNext());
    }

    public IProcessEntityGenerator getSourceProcessEntityGenerator() {
        return sourceProcessEntityGenerator;
    }

    public void setSourceProcessEntityGenerator(IProcessEntityGenerator sourceProcessEntityGenerator) {
        this.sourceProcessEntityGenerator = sourceProcessEntityGenerator;
    }

    public IProcessEntityGenerator getDestProcessEntityGenerator() {
        return destProcessEntityGenerator;
    }

    public void setDestProcessEntityGenerator(IProcessEntityGenerator destProcessEntityGenerator) {
        this.destProcessEntityGenerator = destProcessEntityGenerator;
    }

    public IOperationTypeGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(IOperationTypeGenerator operationTypeGenerator) {
        this.operationTypeGenerator = operationTypeGenerator;
    }

}
