package presidio.input.core.services.converters;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.input.core.services.converters.inputtoade.InputAdeConverter;
import presidio.input.core.services.converters.inputtooutput.InputOutputConverter;
import presidio.input.core.services.impl.SchemaFactory;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.sdk.api.domain.AbstractPresidioDocument;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConverterService {

    @Autowired
    private SchemaFactory converterFactory;

    public List<EnrichedEvent> convertToOutput(List<? extends AbstractPresidioDocument> documents, Schema schema){
        InputOutputConverter inputToOutputConverter = converterFactory.getInputToOutputConverter(String.format("%s.%s", schema.toString(), "input-output-converter"));

        List<EnrichedEvent> convertedOutputRecords = new ArrayList<>();
        documents.forEach(doc -> convertedOutputRecords.add(inputToOutputConverter.convert(doc)));

        return convertedOutputRecords;
    }

    public List<EnrichedRecord> convertToAde(List<? extends AbstractAuditableDocument> documents, Schema schema){

        InputAdeConverter inputToAdeConverter = converterFactory.getInputToAdeConverter(String.format("%s.%s", schema.toString(), "input-ade-converter"));

        List<EnrichedRecord> convertedAdeRecords = new ArrayList<>();
        documents.forEach(doc -> convertedAdeRecords.add(inputToAdeConverter.convert(doc)));

        return convertedAdeRecords;
    }
}
