package presidio.data.generators.common;

import presidio.data.domain.event.OperationType;

/**
 * Created by YaronDL on 8/7/2017.
 */
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
