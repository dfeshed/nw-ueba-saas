package presidio.data.generators.fileop;

import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.NullFileEntityGenerator;

import java.util.HashMap;

public class FileOperationGeneratorTemplateFactory {
    private HashMap<String,String[]> opType2OpCategoryMap = new HashMap<>();

    public FileOperationGeneratorTemplateFactory() {

        String [] fileActionCategory = new String[] {FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value};
        String [] filePermissionChangeCategory = new String[] {FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value};

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

    public IFileOperationGenerator getDefaultFileOperationGenerator() throws GeneratorException {

        FileOperationGenerator generator = new FileOperationGenerator();
        return generator;
    }

    /**
     * "Delete" operations generator
     * Operation type - any FILE_DELETE
     * Destination file - all fields are "null"
     * Operation Type Categories list - only FILE_OPERATION
     **/
    public IFileOperationGenerator getDeleteFileOperationsGenerator() throws GeneratorException {

        FileOperationGenerator generator = new FileOperationGenerator();

        FileOperationTypeCyclicGenerator deleteOpTypeGenerator = new FileOperationTypeCyclicGenerator(new String[] {FILE_OPERATION_TYPE.FILE_DELETED.value});
        generator.setOperationTypeGenerator(deleteOpTypeGenerator);

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        FileOpTypeCategoriesGenerator opTypeCategoriesGenerator = new FileOpTypeCategoriesGenerator(
                opType2OpCategoryMap.get(FILE_OPERATION_TYPE.FILE_DELETED.value));
        generator.setOperationTypeCategoriesGenerator(opTypeCategoriesGenerator);

        return generator;
    }

    /**
     * Operation type - any FILE OPERATION except DELETE
     * Sets operation category according to the operation type
     **/
    public IFileOperationGenerator getFileOperationsGenerator(String operationType) throws GeneratorException {

        FileOperationGenerator generator = new FileOperationGenerator();
        FileOperationTypeCyclicGenerator opTypeGen = new FileOperationTypeCyclicGenerator(
                new String[] {operationType});
        generator.setOperationTypeGenerator(opTypeGen);

        FileOpTypeCategoriesGenerator opTypeCategoriesGenerator = new FileOpTypeCategoriesGenerator(
            opType2OpCategoryMap.get(operationType));
        generator.setOperationTypeCategoriesGenerator(opTypeCategoriesGenerator);

        return generator;
    }








    /**
     * TODO: add templates for other file operation type generators
     */

    /**
     * Default. Can be instantiated directly, without using this factory class.
     */
    public IFileOperationGenerator getDefaultFileOperationsGenerator() throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        return generator;
    }

}

