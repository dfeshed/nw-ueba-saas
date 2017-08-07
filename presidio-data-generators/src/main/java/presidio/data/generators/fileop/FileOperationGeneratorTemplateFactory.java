package presidio.data.generators.fileop;

import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.NullFileEntityGenerator;

import java.util.HashMap;

public class FileOperationGeneratorTemplateFactory {
    private HashMap<String,String[]> opType2OpCategoryMap = new OpTypeCategories().getOpType2OpCategoryMap();

    /**
     * "Delete" operations generator
     * Operation type - any FILE_DELETE
     * Destination file - all fields are "null"
     * Operation Type Categories list - only FILE_OPERATION
     **/
    public IFileOperationGenerator getDeleteFileOperationsGenerator() throws GeneratorException {

        CustomFileOperationGenerator generator = new CustomFileOperationGenerator();

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

        CustomFileOperationGenerator generator = new CustomFileOperationGenerator();
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

    public IFileOperationGenerator getDefaultFileOperationGenerator() throws GeneratorException {

        FileOperationGenerator generator = new FileOperationGenerator();
        return generator;
    }


}

