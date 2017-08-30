package presidio.data.generators.fileop;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.NullFileEntityGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class FileOperationGeneratorTemplateFactory {


    /*********************************    File Action Categories:    *********************************/

    /**
     * "Delete" operations generator
     * Operation type - any FILE_DELETE
     * Destination file - all fields are "null"
     * Operation Type Categories list - only FILE_OPERATION
     **/
    public IFileOperationGenerator createDeleteFileOperationsGenerator() throws GeneratorException {
        return createDeleteFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createDeleteFileOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FILE_DELETED.value, categories));

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    public IFileOperationGenerator createMoveFileOperationsGenerator() throws GeneratorException {
        return createMoveFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createMoveFileOperationsGenerator(List<String> categories) throws GeneratorException {
        return getFileOperationsGenerator(FILE_OPERATION_TYPE.FILE_MOVED.value, categories);
    }
    public IFileOperationGenerator createFailedMoveFileOperationsGenerator() throws GeneratorException {
        return createFailedMoveFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createFailedMoveFileOperationsGenerator(List<String> categories) throws GeneratorException {
        return getFailedFileOperationsGenerator(FILE_OPERATION_TYPE.FILE_MOVED.value, categories);
    }

    public IFileOperationGenerator createRenameFileOperationsGenerator() throws GeneratorException {
        return createRenameFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createRenameFileOperationsGenerator(List<String> categories) throws GeneratorException {
        return getFileOperationsGenerator(FILE_OPERATION_TYPE.FILE_RENAMED.value, categories);
    }

    public IFileOperationGenerator createOpenFileOperationsGenerator() throws GeneratorException {
        return createOpenFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createOpenFileOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FILE_OPENED.value, categories));

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    public IFileOperationGenerator createFailedOpenFileOperationsGenerator() throws GeneratorException {
        return createOpenFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createFailedOpenFileOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FILE_OPENED.value, categories));

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    public IFileOperationGenerator createFolderOpenFileOperationsGenerator() throws GeneratorException {
        return createFolderOpenFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createFolderOpenFileOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FOLDER_OPENED.value, categories));

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    /*********************************    File Permission Change Categories:    *********************************/

    public IFileOperationGenerator createFolderAccessRightsChangedFileOperationsGenerator() throws GeneratorException {
        return createFolderAccessRightsChangedFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createFolderAccessRightsChangedFileOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED.value, categories));

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    public IFileOperationGenerator createLocalSharePermissionsChangeOperationsGenerator() throws GeneratorException {
        return createLocalSharePermissionsChangeOperationsGenerator(null);
    }
    public IFileOperationGenerator createLocalSharePermissionsChangeOperationsGenerator(List<String> categories) throws GeneratorException {
        return getFileOperationsGenerator(FILE_OPERATION_TYPE.LOCAL_SHARE_PERMISSIONS_CHANGED.value, categories);
    }

    public IFileOperationGenerator createFileAccessRightsChangedOperationsGenerator() throws GeneratorException {
        return createFileAccessRightsChangedOperationsGenerator(null);
    }
    public IFileOperationGenerator createFileAccessRightsChangedOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED.value, categories));

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    public IFileOperationGenerator createFileCalssificationChangedOperationsGenerator() throws GeneratorException {
        return createFileCalssificationChangedOperationsGenerator(null);
    }
    public IFileOperationGenerator createFileCalssificationChangedOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED.value, categories));

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    public IFileOperationGenerator createFolderCalssificationChangedOperationsGenerator() throws GeneratorException {
        return createFolderCalssificationChangedOperationsGenerator(null);
    }
    public IFileOperationGenerator createFolderCalssificationChangedOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED.value, categories));

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    public IFileOperationGenerator createFileCentralAccessPolicyChangedOperationsGenerator() throws GeneratorException {
        return createFileCentralAccessPolicyChangedOperationsGenerator(null);
    }
    public IFileOperationGenerator createFileCentralAccessPolicyChangedOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED.value, categories));

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    /**
     * operationType - FILE OPERATION
     * categories - categories that the operation type belong to
     **/
    private IFileOperationGenerator getFileOperationsGenerator(String operationType, List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(operationType, categories));

        return generator;
    }

    private IFileOperationGenerator getFailedFileOperationsGenerator(String operationType, List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(operationType, categories));

        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {OPERATION_RESULT.FAILURE.value}, new int[] {100});
        generator.setOperationResultGenerator(opResultGenerator);

        return generator;
    }

    private FixedFileOperationTypeGenerator getFixedFileOperationTypeGenerator(String operationType, List<String> categories){
        return new FixedFileOperationTypeGenerator(new OperationType(operationType, getOperationTypeCategrories(operationType, categories)));
    }

    private List<String> getOperationTypeCategrories(String operationType, List<String> categories){
        List<String> ret = new ArrayList<>();
        if(categories != null){
            ret.addAll(categories);
        }
        ret.addAll(getOperationTypeCategrories(operationType));
        return ret == null ? Collections.emptyList() : ret;
    }

    protected List<String> getOperationTypeCategrories(String operationType){
        return Collections.emptyList();
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

