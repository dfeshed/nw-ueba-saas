package presidio.data.generators.file;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.domain.event.file.FILE_OPERATION_TYPE;

/**
 * This class is one element data provider from a cyclic list of string values - ADE File
 */
public class FileOperationTypeCyclicGenerator extends AbstractCyclicValuesGenerator {

    private static final String[] DEFAULT_FILE_OPERATION_TYPE = {
            FILE_OPERATION_TYPE.FOLDER_OPENED.value,
            FILE_OPERATION_TYPE.FILE_DELETED.value,
            FILE_OPERATION_TYPE.FILE_OPENED.value,
            FILE_OPERATION_TYPE.FILE_RENAMED.value,
            FILE_OPERATION_TYPE.FILE_MOVED.value,
            FILE_OPERATION_TYPE.LOCAL_SHARE_PERMISSIONS_CHANGED.value,
            FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED.value,
            FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED.value,
            FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED.value,
            FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED.value
            };

    public FileOperationTypeCyclicGenerator() {
        super(DEFAULT_FILE_OPERATION_TYPE);
    }

    public FileOperationTypeCyclicGenerator(String[] customList) {
        super(customList);
    }
}
