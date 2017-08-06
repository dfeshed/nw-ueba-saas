package presidio.data.generators.activedirectoryop;

import presidio.data.generators.common.*;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.domain.event.activedirectory.ActiveDirectoryOperation;

public class ActiveDirectoryOperationGenerator implements IActiveDirectoryOperationGenerator{

    private IStringGenerator operationTypeGenerator;
    private IStringListGenerator operationTypeCategoriesGenerator;
    private IStringGenerator objectNameGenerator;
    private IStringGenerator resultGenerator;
    private IStringGenerator resultCodeGenerator;

    public ActiveDirectoryOperationGenerator() throws GeneratorException {

        this.operationTypeGenerator = new ActiveDirOperationTypeCyclicGenerator();
        this.operationTypeCategoriesGenerator = new ActiveDirectoryOpTypeCategoriesGenerator();
        this.objectNameGenerator = new RandomStringGenerator(20);   // random string, 20 chars length
        this.resultGenerator = new OperationResultPercentageGenerator();    // 100% Success
        this.resultCodeGenerator = new RandomStringGenerator(6);
    }

    public IStringGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(IStringGenerator operationTypeGenerator) {
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

    public IStringListGenerator getOperationTypeCategoriesGenerator() {
        return operationTypeCategoriesGenerator;
    }

    public void setOperationTypeCategoriesGenerator(IStringListGenerator operationTypeCategoriesGenerator) {
        this.operationTypeCategoriesGenerator = operationTypeCategoriesGenerator;
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
                (String) getOperationTypeGenerator().getNext(),
                getOperationTypeCategoriesGenerator().getNext(),
                (String) getObjectNameGenerator().getNext(),
                (String) getResultGenerator().getNext(),
                (String) getResultCodeGenerator().getNext());

    }
}
