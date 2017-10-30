package presidio.data.generators.activedirectoryop;

import presidio.data.domain.event.OperationType;
import presidio.data.generators.common.*;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.domain.event.activedirectory.ActiveDirectoryOperation;

public class ActiveDirectoryOperationGenerator implements IActiveDirectoryOperationGenerator{

    private IOperationTypeGenerator operationTypeGenerator;
    private IStringGenerator objectNameGenerator;
    private IStringGenerator resultGenerator;
    private IStringGenerator resultCodeGenerator;

    public ActiveDirectoryOperationGenerator() throws GeneratorException {

        this.operationTypeGenerator = new ActiveDirOperationTypeCyclicGenerator();
        this.objectNameGenerator = new RandomStringGenerator(20);   // random string, 20 chars length
        this.resultGenerator = new OperationResultPercentageGenerator();    // 100% Success
        this.resultCodeGenerator = new RandomStringGenerator(6);
    }

    public IOperationTypeGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(IOperationTypeGenerator operationTypeGenerator) {
        this.operationTypeGenerator = operationTypeGenerator;
    }

    public IStringGenerator getObjectNameGenerator() {
        return objectNameGenerator;
    }

    public void setObjectNameGenerator(IStringGenerator objectNameGenerator) {
        this.objectNameGenerator = objectNameGenerator;
    }

    public IStringGenerator getResultGenerator() {
        return resultGenerator;
    }

    public void setResultGenerator(IStringGenerator resultGenerator) {
        this.resultGenerator = resultGenerator;
    }

    public IStringGenerator getResultCodeGenerator() {
        return resultCodeGenerator;
    }

    public void setResultCodeGenerator(IStringGenerator resultCodeGenerator) {
        this.resultCodeGenerator = resultCodeGenerator;
    }

    @Override
    public ActiveDirectoryOperation getNext() {
        return new ActiveDirectoryOperation(
                getOperationTypeGenerator().getNext(),
                getObjectNameGenerator().getNext(),
                getResultGenerator().getNext(),
                getResultCodeGenerator().getNext());

    }
}
