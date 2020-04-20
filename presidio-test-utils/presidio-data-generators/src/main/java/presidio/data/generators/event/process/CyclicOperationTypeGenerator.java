package presidio.data.generators.event.process;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.process.PROCESS_OPERATION_TYPE;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IOperationTypeGenerator;

import java.util.Collections;

public class CyclicOperationTypeGenerator extends CyclicValuesGenerator implements IOperationTypeGenerator {

    private final static String[] DEFAULT_VALUES = PROCESS_OPERATION_TYPE.getNames(PROCESS_OPERATION_TYPE.class);

    public CyclicOperationTypeGenerator() {
        super(DEFAULT_VALUES);
    }

    public CyclicOperationTypeGenerator(String[] customList) {
        super(customList);
    }

    @Override
    public OperationType getNext() {
        String opTypeName = super.getNext().toString();
        return new OperationType(opTypeName, Collections.emptyList());
    }
}
