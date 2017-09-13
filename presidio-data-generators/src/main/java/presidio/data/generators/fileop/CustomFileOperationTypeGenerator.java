package presidio.data.generators.fileop;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.common.CyclicValuesGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by YaronDL on 8/7/2017.
 */
public class CustomFileOperationTypeGenerator extends CyclicValuesGenerator<OperationType> implements IFileOperationTypeGenerator{

    String[] customOpTypesDefault = new String[] {
            "LOCAL_SHARE_REMOVED", "LOCAL_SHARE_PERMISSIONS_CHANGED", "LOCAL_SHARE_FOLDER_PATH_CHANGED", "LOCAL_SHARE_ADDED", "JUNCTION_POINT_DELETED", "JUNCTION_POINT_CREATED", "FOLDER_RENAMED", "FOLDER_OWNERSHIP_CHANGED", "FOLDER_OPENED", "FOLDER_MOVED", "FOLDER_DELETED", "FOLDER_CREATED", "FOLDER_CLASSIFICATION_CHANGED", "FOLDER_CENTRAL_ACCESS_POLICY_CHANGED", "FOLDER_AUDITING_CHANGED", "FOLDER_ATTRIBUTE_CHANGED", "FOLDER_ACCESS_RIGHTS_CHANGED", "FILE_RENAMED", "FILE_OWNERSHIP_CHANGED", "FILE_OPENED", "FILE_MOVED", "FILE_DELETED", "FILE_CREATED", "FILE_CLASSIFICATION_CHANGED", "FILE_CENTRAL_ACCESS_POLICY_CHANGED", "FILE_AUDITING_CHANGED", "FILE_ATTRIBUTE_CHANGED", "FILE_ACCESS_RIGHTS_CHANGED", "FAILED_SHARE_ACCESS", "FAILED_FOLDER_ACCESS", "FAILED_FILE_ACCESS", "NET_APP_FOLDER_RENAMED", "NET_APP_FOLDER_OWNERSHIP_CHANGED", "NET_APP_FOLDER_MOVED", "NET_APP_FOLDER_DELETED", "NET_APP_FOLDER_CREATED", "NET_APP_FOLDER_ACCESS_RIGHTS_CHANGED", "NET_APP_FILE_RENAMED", "NET_APP_FILE_OWNERSHIP_CHANGED", "NET_APP_FILE_OPENED", "NET_APP_FILE_MOVED", "NET_APP_FILE_DELETED", "NET_APP_FILE_CREATED", "NET_APP_FILE_ACCESS_RIGHTS_CHANGED", "EMC_FOLDER_RENAMED", "EMC_FOLDER_OWNERSHIP_CHANGED", "EMC_FOLDER_MOVED", "EMC_FOLDER_DELETED", "EMC_FOLDER_CREATED", "EMC_FOLDER_ACCESS_RIGHTS_CHANGED", "EMC_FILE_RENAMED", "EMC_FILE_OWNERSHIP_CHANGED", "EMC_FILE_OPENED", "EMC_FILE_MOVED", "EMC_FILE_DELETED", "EMC_FILE_CREATED", "EMC_FILE_ACCESS_RIGHTS_CHANGED", "CEPP_CONFIGURATION_CHANGED", "FLUIDFS_FOLDER_RENAMED", "FLUIDFS_FOLDER_OWNERSHIP_CHANGED", "FLUIDFS_FOLDER_MOVED", "FLUIDFS_FOLDER_DELETED", "FLUIDFS_FOLDER_CREATED", "FLUIDFS_FOLDER_AUDITING_CHANGED", "FLUIDFS_FOLDER_ACCESS_RIGHTS_CHANGED", "FLUIDFS_FILE_RENAMED", "FLUIDFS_FILE_OWNERSHIP_CHANGED", "FLUIDFS_FILE_OPENED", "FLUIDFS_FILE_MOVED", "FLUIDFS_FILE_DELETED", "FLUIDFS_FILE_CREATED", "FLUIDFS_FILE_CONTENTS_WRITTEN", "FLUIDFS_FILE_AUDITING_CHANGED", "FLUIDFS_FILE_ACCESS_RIGHTS_CHANGED"
    };

    public CustomFileOperationTypeGenerator()
    {
        super();
        OperationType[] opTypes = buildValues(customOpTypesDefault);
        super.setValuesList(opTypes);
    }

    public CustomFileOperationTypeGenerator(String[] operationTypeNames)
    {
        super();
        OperationType[] opTypes = buildValues(operationTypeNames);
        super.setValuesList(opTypes);
    }

    private OperationType[] buildValues(String[] operationTypeNames) {
        OperationType opTypes[] = new OperationType[operationTypeNames.length];

        int i = 0;
        for (String name : operationTypeNames) {
            List<String> categories = new ArrayList<>();
            if (getOpType2OpCategoryMap().containsKey(name)) {
                categories = getOpType2OpCategoryMap().get(name);
            }
            else {
                categories.add("NA");
            }
            opTypes[i] = new OperationType(name, categories);
            i++;
        }
        return opTypes;
    }

    private static HashMap<String, List<String>> getOpType2OpCategoryMap() {
        HashMap<String, List<String>> opType2OpCategoryMap = new HashMap<>();
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_OPENED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_DELETED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_OPENED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_RENAMED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_MOVED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));

        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.LOCAL_SHARE_PERMISSIONS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));

        return opType2OpCategoryMap;
    }
}
