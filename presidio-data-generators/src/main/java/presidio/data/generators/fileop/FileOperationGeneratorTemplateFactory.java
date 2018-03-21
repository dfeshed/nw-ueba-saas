package presidio.data.generators.fileop;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.generators.common.FixedOperationTypeGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.fileentity.EmptyFileEntityGenerator;
import presidio.data.generators.fileentity.FileEntityGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.NullFileEntityGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public IFileOperationGenerator createDownloadFileOperationsGenerator() throws GeneratorException {
        return createDownloadFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createDownloadFileOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FILE_DOWNLOADED.value, categories));

        IFileEntityGenerator nullFileEntityGenerator = new NullFileEntityGenerator();
        generator.setDestFileEntityGenerator(nullFileEntityGenerator);

        return generator;
    }

    public IFileOperationGenerator createCopyFileOperationsGenerator() throws GeneratorException {
        return createCopyFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createCopyFileOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FILE_COPIED.value, categories));

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

    public IFileOperationGenerator createMoveFromSharedFileOperationsGenerator() throws GeneratorException {
        return createMoveFromSharedFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createMoveFromSharedFileOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FILE_MOVED.value, categories));

        // Source file - shared
        FileEntityGenerator srcFileGenerator = new FileEntityGenerator();
        BooleanPercentageGenerator isSrcDriveSharedGenerator = new BooleanPercentageGenerator(100);
        srcFileGenerator.setIsDriveSharedGenerator(isSrcDriveSharedGenerator);
        generator.setSourceFileEntityGenerator(srcFileGenerator);

        // Destination file - local
        FileEntityGenerator dstFileGenerator = new FileEntityGenerator();
        BooleanPercentageGenerator isDstDriveSharedGenerator = new BooleanPercentageGenerator(0);
        dstFileGenerator.setIsDriveSharedGenerator(isDstDriveSharedGenerator);
        generator.setDestFileEntityGenerator(dstFileGenerator);

        return generator;
    }

    public IFileOperationGenerator createMoveToSharedFileOperationsGenerator() throws GeneratorException {
        return createMoveToSharedFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createMoveToSharedFileOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(FILE_OPERATION_TYPE.FILE_MOVED.value, categories));

        // Source file - local
        FileEntityGenerator srcFileGenerator = new FileEntityGenerator();
        BooleanPercentageGenerator isSrcDriveSharedGenerator = new BooleanPercentageGenerator(0);
        srcFileGenerator.setIsDriveSharedGenerator(isSrcDriveSharedGenerator);
        generator.setSourceFileEntityGenerator(srcFileGenerator);

        // Destination file - shared
        FileEntityGenerator dstFileGenerator = new FileEntityGenerator();
        BooleanPercentageGenerator isDstDriveSharedGenerator = new BooleanPercentageGenerator(100);
        dstFileGenerator.setIsDriveSharedGenerator(isDstDriveSharedGenerator);
        generator.setDestFileEntityGenerator(dstFileGenerator);

        return generator;
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

    public IFileOperationGenerator createFailedRenameFileOperationsGenerator() throws GeneratorException {
        return createFailedRenameFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createFailedRenameFileOperationsGenerator(List<String> categories) throws GeneratorException {
        return getFailedFileOperationsGenerator(FILE_OPERATION_TYPE.FILE_RENAMED.value, categories);
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
        return createFailedOpenFileOperationsGenerator(null);
    }
    public IFileOperationGenerator createFailedOpenFileOperationsGenerator(List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = getFailedFileOperationsGenerator(FILE_OPERATION_TYPE.FILE_OPENED.value, categories);

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

        IFileEntityGenerator emptyFileEntityGenerator = new EmptyFileEntityGenerator();
        generator.setDestFileEntityGenerator(emptyFileEntityGenerator);

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

    public IFileOperationGenerator createFailedLocalSharePermissionsChangeOperationsGenerator() throws GeneratorException {
        return createFailedLocalSharePermissionsChangeOperationsGenerator(null);
    }
    public IFileOperationGenerator createFailedLocalSharePermissionsChangeOperationsGenerator(List<String> categories) throws GeneratorException {
        return getFailedFileOperationsGenerator(FILE_OPERATION_TYPE.LOCAL_SHARE_PERMISSIONS_CHANGED.value, categories);
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

    private FileOperationGenerator getFailedFileOperationsGenerator(String operationType, List<String> categories) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();
        generator.setOperationTypeGenerator(getFixedFileOperationTypeGenerator(operationType, categories));

        OperationResultPercentageGenerator opResultGenerator = new OperationResultPercentageGenerator(new String[] {OPERATION_RESULT.FAILURE.value}, new int[] {100});
        generator.setOperationResultGenerator(opResultGenerator);

        return generator;
    }

    private FixedOperationTypeGenerator getFixedFileOperationTypeGenerator(String operationType, List<String> categories){
        return new FixedOperationTypeGenerator(new OperationType(operationType, getOperationTypeCategrories(operationType, categories)));
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

