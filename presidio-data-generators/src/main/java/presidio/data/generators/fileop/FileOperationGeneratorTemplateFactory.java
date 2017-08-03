package presidio.data.generators.fileop;

import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.NullFileEntityGenerator;

public class FileOperationGeneratorTemplateFactory {
    public IFileOperationGenerator getDefaultFileOperationGenerator() throws GeneratorException {

        FileOperationGenerator generator = new FileOperationGenerator();
        return generator;
    }

    /**
     * "Delete" operations generator
     * Operation type - FILE_DELETED
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
                new String[] {FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value});
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

