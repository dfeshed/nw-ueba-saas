package presidio.data.generators.fileop;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.common.CyclicValuesGenerator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by YaronDL on 8/7/2017.
 */
public class CyclicFileOperationTypeGenerator extends CyclicValuesGenerator<OperationType> implements IFileOperationTypeGenerator{

    public CyclicFileOperationTypeGenerator(OperationType[] operationTypes){
        super(operationTypes);
    }
    public CyclicFileOperationTypeGenerator(){
        super();
        OperationType opTypes[] = new OperationType[FILE_OPERATION_TYPE.values().length];
        int i = 0;
        for (FILE_OPERATION_TYPE type : FILE_OPERATION_TYPE.values()) {
            opTypes[i] = new OperationType(type.value, getOpType2OpCategoryMap().get(type.value));
            i++;
        }
        super.setValuesList(opTypes);
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
