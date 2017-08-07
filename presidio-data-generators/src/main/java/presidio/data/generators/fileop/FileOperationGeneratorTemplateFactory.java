package presidio.data.generators.fileop;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.NullFileEntityGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FileOperationGeneratorTemplateFactory {
    private HashMap<String,List<String>> opType2OpCategoryMap = getOpType2OpCategoryMap();
    /**
     * "Delete" operations generator
     * Operation type - any FILE_DELETE
     * Destination file - all fields are "null"
     * Operation Type Categories list - only FILE_OPERATION
     **/
    public IFileOperationGenerator createDeleteFileOperationsGenerator() throws GeneratorException {

        FileOperationGenerator generator = new FileOperationGenerator();
        FixedFileOperationTypeGenerator fixedFileOperationTypeGenerator = new FixedFileOperationTypeGenerator(new OperationType(FILE_OPERATION_TYPE.FILE_DELETED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value)));
        generator.setOperationTypeGenerator(fixedFileOperationTypeGenerator);

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    public IFileOperationGenerator createMoveFileOperationsGenerator() throws GeneratorException {
        return getFileOperationsGenerator(FILE_OPERATION_TYPE.FILE_MOVED.value);
    }

    public IFileOperationGenerator createLocalSharePermissionsChangeOperationsGenerator() throws GeneratorException {
        return getFileOperationsGenerator(FILE_OPERATION_TYPE.LOCAL_SHARE_PERMISSIONS_CHANGED.value);
    }

    /**
     * Operation type - any FILE OPERATION except DELETE
     * Sets operation category according to the operation type
     **/
    private IFileOperationGenerator getFileOperationsGenerator(String operationType) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(operationType));

        return generator;
    }

    private FixedFileOperationTypeGenerator getFixedFileOperationTypeGenerator(String operationType){
        return new FixedFileOperationTypeGenerator(new OperationType(operationType, getOperationTypeCategrories(operationType)));
    }

    private List<String> getOperationTypeCategrories(String operationType){
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

        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.LOCAL_SHARE_PERMISSIONS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        opType2OpCategoryMap.put(FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED.value, Collections.singletonList(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value));
        return opType2OpCategoryMap;
    }

    /**
     * TODO: add templates for other file operation type generators
     */

    /**
     * Default. Can be instantiated directly, without using this factory class.
     */

    public IFileOperationGenerator getDefaultFileOperationGenerator() throws GeneratorException {

        FileOperationGenerator generator = new FileOperationGenerator();
        return generator;
    }


}

