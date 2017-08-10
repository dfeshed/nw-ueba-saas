package presidio.input.core.services.converters.inputtoade;

import fortscale.domain.core.AbstractAuditableDocument;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.sdk.api.domain.rawevents.FileRawEvent;

public class FileConverter implements InputAdeConverter {

    @Override
    public EnrichedRecord convert(AbstractAuditableDocument document) {
        FileRawEvent fileRawEvent = (FileRawEvent) document;
        EnrichedFileRecord adeRecord = new EnrichedFileRecord(fileRawEvent.getDateTime());
        return adeRecord;
    }
}
