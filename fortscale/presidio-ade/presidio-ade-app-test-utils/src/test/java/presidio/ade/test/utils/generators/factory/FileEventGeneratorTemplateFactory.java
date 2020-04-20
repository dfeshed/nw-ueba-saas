package presidio.ade.test.utils.generators.factory;

import presidio.ade.test.utils.generators.MultiFileEventGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.ITimeGeneratorFactory;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.SingleAdminUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YaronDL on 10/22/2017.
 */
public class FileEventGeneratorTemplateFactory {

    public MultiFileEventGenerator createMultiFileEventGenerator(ITimeGeneratorFactory timeGeneratorFactory, IStringGenerator contextIdGenerator, List<IFileOperationGenerator> fileOperationGeneratorList) throws GeneratorException {
        return createMultiFileEventGenerator(timeGeneratorFactory, false,contextIdGenerator, fileOperationGeneratorList);
    }

    public MultiFileEventGenerator createMultiFileEventGenerator(ITimeGeneratorFactory timeGeneratorFactory, boolean isAdminUser, IStringGenerator contextIdGenerator, List<IFileOperationGenerator> fileOperationGeneratorList) throws GeneratorException {
        List<FileEventsGenerator> fileEventsGenerators = new ArrayList<>();
        for(IFileOperationGenerator fileOperationGenerator: fileOperationGeneratorList){
            String contextId = contextIdGenerator.getNext();
            IUserGenerator userGenerator = isAdminUser ? new SingleAdminUserGenerator(contextId) : new SingleUserGenerator(contextId);
            FileEventsGenerator generator = createFileEventsGenerator(timeGeneratorFactory.createTimeGenerator(), userGenerator, fileOperationGenerator);
            fileEventsGenerators.add(generator);
        }

        return new MultiFileEventGenerator(fileEventsGenerators);
    }

    public MultiFileEventGenerator createMultiFileEventGenerator(ITimeGeneratorFactory timeGeneratorFactory, IUserGenerator userGenerator, List<IFileOperationGenerator> fileOperationGeneratorList) throws GeneratorException {
        List<FileEventsGenerator> fileEventsGenerators = new ArrayList<>();
        for(IFileOperationGenerator fileOperationGenerator: fileOperationGeneratorList){
            FileEventsGenerator generator = createFileEventsGenerator(timeGeneratorFactory.createTimeGenerator(), userGenerator, fileOperationGenerator);
            fileEventsGenerators.add(generator);
        }

        return new MultiFileEventGenerator(fileEventsGenerators);
    }

    public FileEventsGenerator createFileEventsGenerator(ITimeGenerator timeGenerator, IUserGenerator userGenerator, IFileOperationGenerator fileOperationGenerator) throws GeneratorException {
        FileEventsGenerator generator = new FileEventsGenerator();
        generator.setTimeGenerator(timeGenerator);
        generator.setUserGenerator(userGenerator);
        generator.setFileOperationGenerator(fileOperationGenerator);

        return generator;
    }
}
