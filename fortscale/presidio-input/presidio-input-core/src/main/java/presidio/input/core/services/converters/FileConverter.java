package presidio.input.core.services.converters;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.core.FileRawEvent;
import presidio.ade.domain.record.enriched.EnrichedFileRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;

public class FileConverter implements InputAdeConverter {

    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        FileRawEvent fileRawEvent = (FileRawEvent) document;
        EnrichedFileRecord adeRecord = new EnrichedFileRecord(fileRawEvent.getDateTime());
        return adeRecord;
    }
}
