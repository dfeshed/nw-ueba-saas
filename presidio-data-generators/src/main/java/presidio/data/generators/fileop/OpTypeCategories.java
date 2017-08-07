package presidio.data.generators.fileop;

import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;

import java.util.HashMap;

public class OpTypeCategories {

    private HashMap<String,String[]> opType2OpCategoryMap;
    private static final String [] fileActionCategory = new String[] {FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value};
    private static final String [] filePermissionChangeCategory = new String[] {FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value};

    public HashMap<String, String[]> getOpType2OpCategoryMap() {
        if (opType2OpCategoryMap == null) {
            opType2OpCategoryMap = new HashMap<>();
            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_OPENED.value, fileActionCategory);
            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_DELETED.value, fileActionCategory);
            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_OPENED.value, fileActionCategory);
            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_RENAMED.value, fileActionCategory);
            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_MOVED.value, fileActionCategory);

            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.LOCAL_SHARE_PERMISSIONS_CHANGED.value, filePermissionChangeCategory);
            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED.value, filePermissionChangeCategory);
            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED.value, filePermissionChangeCategory);
            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED.value, filePermissionChangeCategory);
            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED.value, filePermissionChangeCategory);
            opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED.value, filePermissionChangeCategory);
        }
        return opType2OpCategoryMap;
    }
}
