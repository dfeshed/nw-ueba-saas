package presidio.data.generators.file;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;

/**
 * This class is one element data provider from a cyclic list of string values
 */
public class OperationTypeCyclicGenerator extends AbstractCyclicValuesGenerator {
    private static final String[] DEFAULT_OPERATION_TYPE = {DEFAULT_EVENT_TYPE.FILE_MOVE.value,DEFAULT_EVENT_TYPE.FILE_COPY.value,DEFAULT_EVENT_TYPE.FILE_DELETE.value,DEFAULT_EVENT_TYPE.FILE_RECYCLE.value,DEFAULT_EVENT_TYPE.FILE_OPEN.value};

    public OperationTypeCyclicGenerator() {
        super(DEFAULT_OPERATION_TYPE);
    }

    public OperationTypeCyclicGenerator(String[] customList) {
        super(customList);
    }
}
