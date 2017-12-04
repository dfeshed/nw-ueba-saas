package presidio.data.generators.common;

import presidio.data.domain.event.OperationType;

public class FixedOperationTypeGenerator implements IOperationTypeGenerator {
    private OperationType operationType;

    public FixedOperationTypeGenerator(OperationType operationType){
        this.operationType = operationType;
    }

    @Override
    public OperationType getNext() {
        return operationType;
    }
}
