package presidio.ade.test.utils.generators.factory;

import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.test.utils.generators.MultiFileEventGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.ITimeGeneratorFactory;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YaronDL on 10/22/2017.
 */
public class FileEventGeneratorTemplateFactory {

    public MultiFileEventGenerator createMultiFileEventGenerator(ITimeGeneratorFactory timeGeneratorFactory, IStringGenerator contextIdGenerator, List<IFileOperationGenerator> fileOperationGeneratorList) throws GeneratorException {
        List<FileEventsGenerator> fileEventsGenerators = new ArrayList<>();
        for(IFileOperationGenerator fileOperationGenerator: fileOperationGeneratorList){
            FileEventsGenerator generator = new FileEventsGenerator();
            String contextId = contextIdGenerator.getNext();
            generator.setTimeGenerator(timeGeneratorFactory.createTimeGenerator());
            generator.setUserGenerator(new SingleUserGenerator(contextId));
            generator.setFileOperationGenerator(fileOperationGenerator);
            fileEventsGenerators.add(generator);
        }

        return new MultiFileEventGenerator(fileEventsGenerators);
    }
}
