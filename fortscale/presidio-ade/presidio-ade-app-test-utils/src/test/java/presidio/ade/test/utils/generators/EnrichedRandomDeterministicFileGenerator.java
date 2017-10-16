package presidio.ade.test.utils.generators;

import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.test.utils.converters.FileRaw2EnrichedConverter;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileop.*;
import presidio.data.generators.user.SingleUserGenerator;

import java.util.ArrayList;
import java.util.List;

public class EnrichedRandomDeterministicFileGenerator implements IEventGenerator<EnrichedFileRecord> {


    protected final FileRaw2EnrichedConverter converter;
    private IStringGenerator contextIdGenerator;
    private TimeGenerator timeGenerator;
    private FileOperationGenerator fileOperationTpeGenerator;


    public EnrichedRandomDeterministicFileGenerator(TimeGenerator timeGenerator, IStringGenerator contextIdGenerator, FileOperationGenerator fileOperationTpeGenerator) {
        this.contextIdGenerator = contextIdGenerator;
        this.timeGenerator = timeGenerator;
        this.fileOperationTpeGenerator = fileOperationTpeGenerator;
        this.converter = new FileRaw2EnrichedConverter();
    }


    /**
     * Generate events.
     *
     * @throws GeneratorException
     */
    @Override
    public List<EnrichedFileRecord> generate() throws GeneratorException {

        List<EnrichedFileRecord> enrichedRecords = new ArrayList<>();

        FileEventsGenerator generator = new FileEventsGenerator();

        while (fileOperationTpeGenerator.hasNext()) {
            String contextId = contextIdGenerator.getNext();
            timeGenerator.reset();
            generator.setTimeGenerator(timeGenerator);
            generator.setUserGenerator(new SingleUserGenerator(contextId));
            IFileOperationGenerator fileOperationGenerator = fileOperationTpeGenerator.getNext();
            generator.setFileOperationGenerator(fileOperationGenerator);
            List<FileEvent> events = generator.generate();
            enrichedRecords.addAll(converter.convert(events));
        }

        return enrichedRecords;
    }

}
