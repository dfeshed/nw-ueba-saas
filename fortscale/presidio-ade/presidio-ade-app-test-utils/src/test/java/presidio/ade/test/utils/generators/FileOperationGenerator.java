package presidio.ade.test.utils.generators;

import presidio.data.ade.AdeFileOperationGeneratorTemplateFactory;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileop.IFileOperationGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maria_dorohin on 10/3/17.
 */
public class FileOperationGenerator {

    private int currentIdx;
    private List<IFileOperationGenerator> values;

    public FileOperationGenerator() throws GeneratorException {
        List<IFileOperationGenerator> fileOperationGeneratorsList = getFileOperationGenerator();
        values = fileOperationGeneratorsList;
        this.currentIdx = 0;
    }


    public FileOperationGenerator(List<IFileOperationGenerator> fileOperationGenerators) throws GeneratorException {
        values = fileOperationGenerators;
    }

    public boolean hasNext() {
        return (currentIdx < values.size());
    }

    public IFileOperationGenerator getNext() throws GeneratorException {
        if (!hasNext())
            throw new GeneratorException("Time Generator Exception occurred: End of the LocalTime interval is reached - no more data.");
        return values.get(currentIdx++);
    }

    /**
     * Get fileOperationGenerators that cover all the features
     *
     * @return list of fileOperationGenerators
     * @throws GeneratorException
     */
    private List<IFileOperationGenerator> getFileOperationGenerator() throws GeneratorException {
        List<IFileOperationGenerator> fileOperationGenerators = new ArrayList<>();
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createLocalSharePermissionsChangeOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFailedLocalSharePermissionsChangeOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFailedOpenFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createOpenFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFolderOpenFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createDeleteFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createRenameFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFailedRenameFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createMoveFromSharedFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createMoveToSharedFileOperationsGenerator());

        return fileOperationGenerators;
    }

}
