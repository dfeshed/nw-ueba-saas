package presidio.data.generators.fileop;

import presidio.data.domain.event.OperationType;

/**
 * Created by YaronDL on 8/7/2017.
 */
public class FixedFileOperationTypeGenerator implements IFileOperationTypeGenerator{
    private OperationType operationType;

    public FixedFileOperationTypeGenerator(OperationType operationType){
        this.operationType = operationType;
    }

    @Override
    public OperationType getNext() {
        return operationType;
    }
}
