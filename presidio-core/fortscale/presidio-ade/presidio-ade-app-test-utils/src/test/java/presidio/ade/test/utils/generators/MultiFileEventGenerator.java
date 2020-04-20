package presidio.ade.test.utils.generators;

import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.test.utils.converters.FileRaw2EnrichedConverter;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;

import java.util.ArrayList;
import java.util.List;

//public class MultiFileEventGenerator implements IEventGenerator<EnrichedFileRecord> {
public class MultiFileEventGenerator{

    protected final FileRaw2EnrichedConverter converter;
    private List<FileEventsGenerator> fileEventsGenerators;


    public MultiFileEventGenerator(List<FileEventsGenerator> fileEventsGenerators) {
        this.fileEventsGenerators = fileEventsGenerators;
        this.converter = new FileRaw2EnrichedConverter();
    }


    /**
     * Generate events.
     *
     * @throws GeneratorException
     */
    public List<EnrichedFileRecord> generate() throws GeneratorException {
        List<EnrichedFileRecord> enrichedRecords = new ArrayList<>();
        for (FileEventsGenerator fileEventsGenerator: fileEventsGenerators) {
            List<FileEvent> events = fileEventsGenerator.generate();
            enrichedRecords.addAll(converter.convert(events));
        }

        return enrichedRecords;
    }

}
