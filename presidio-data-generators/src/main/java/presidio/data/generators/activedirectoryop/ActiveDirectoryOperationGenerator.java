package presidio.data.generators.activedirectoryop;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IBooleanGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.domain.event.activedirectory.ActiveDirectoryOperation;

public class ActiveDirectoryOperationGenerator implements IActiveDirectoryOperationGenerator{

    private ActiveDirOperationTypeCyclicGenerator operationTypeGenerator;
    private IBooleanGenerator isSecuritySensitiveOperationGenerator;
    private RandomStringGenerator objectNameGenerator;
    private OperationResultPercentageGenerator resultGenerator;

    public ActiveDirectoryOperationGenerator() throws GeneratorException {

        this.operationTypeGenerator = new ActiveDirOperationTypeCyclicGenerator();
        this.isSecuritySensitiveOperationGenerator = new BooleanPercentageGenerator(1); // 1% of operations are sensitive
        this.objectNameGenerator = new RandomStringGenerator(20);   // random string, 20 chars length
        this.resultGenerator = new OperationResultPercentageGenerator();    // 100% Success

    }

    public ActiveDirOperationTypeCyclicGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(ActiveDirOperationTypeCyclicGenerator operationTypeGenerator) {
        this.operationTypeGenerator = operationTypeGenerator;
    }

    public IBooleanGenerator getIsSecuritySensitiveOperationGenerator() {
        return isSecuritySensitiveOperationGenerator;
    }

    public void setIsSecuritySensitiveOperationGenerator(IBooleanGenerator isSecuritySensitiveOperationGenerator) {
        this.isSecuritySensitiveOperationGenerator = isSecuritySensitiveOperationGenerator;
    }

    public RandomStringGenerator getObjectNameGenerator() {
        return objectNameGenerator;
    }

    public void setObjectNameGenerator(RandomStringGenerator objectNameGenerator) {
        this.objectNameGenerator = objectNameGenerator;
    }

    public OperationResultPercentageGenerator getResultGenerator() {
        return resultGenerator;
    }

    public void setResultGenerator(OperationResultPercentageGenerator resultGenerator) {
        this.resultGenerator = resultGenerator;
    }

    @Override
    public ActiveDirectoryOperation getNext() {
        return new ActiveDirectoryOperation(
                (String) getOperationTypeGenerator().getNext(),
                (Boolean)getIsSecuritySensitiveOperationGenerator().getNext(),
                getObjectNameGenerator().getNext(),
                (String) getResultGenerator().getNext());

    }
}
