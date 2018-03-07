package presidio.data.generators.fileop;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IOperationTypeGenerator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CyclicOperationTypeGenerator extends CyclicValuesGenerator<OperationType> implements IOperationTypeGenerator {

    public CyclicOperationTypeGenerator(OperationType[] operationTypes){
        super(operationTypes);
    }
    public CyclicOperationTypeGenerator(){
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
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_COPY.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CHECKED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_PREVIEW.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_MODIFIED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_RESTORE.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_UPLOAD.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_DOWNLOAD.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));

        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.LOCAL_SHARE_PERMISSIONS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        return opType2OpCategoryMap;
    }
}
