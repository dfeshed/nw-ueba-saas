package presidio.data.ade;

import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.fileop.FileOperationGeneratorTemplateFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AdeFileOperationGeneratorTemplateFactory extends FileOperationGeneratorTemplateFactory{
    private HashMap<String,List<String>> opType2OpCategoryMap = getOpType2OpCategoryMap();

    @Override
    protected List<String> getOperationTypeCategrories(String operationType){
        List<String> categories = opType2OpCategoryMap.get(operationType);
        return categories == null ? Collections.emptyList() : categories;
    }

    private static HashMap<String, List<String>> getOpType2OpCategoryMap() {
        HashMap<String, List<String>> opType2OpCategoryMap = new HashMap<>();
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_OPENED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_DELETED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_OPENED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_RENAMED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_MOVED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_COPIED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CHECKED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_PREVIEWED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_MODIFIED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_RESTORED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_UPLOADED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_DOWNLOADED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_ACCESSED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CHECKED_IN.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CHECKED_OUT_DISCARDED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CHECKED_OUT.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_ACCESSED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_MODIFIED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));

        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.LOCAL_SHARE_PERMISSIONS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        return opType2OpCategoryMap;
    }
}

