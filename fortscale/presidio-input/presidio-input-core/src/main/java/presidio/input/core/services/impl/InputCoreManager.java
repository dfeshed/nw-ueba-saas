package presidio.input.core.services.impl;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import presidio.input.core.RawEventsPageIterator;
import presidio.input.core.services.converters.inputtoade.InputAdeConverter;
import presidio.input.core.services.converters.inputtooutput.InputOutputConverter;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.transformation.managers.TransformationManager;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class InputCoreManager {

    private static final Logger logger = Logger.getLogger(InputCoreManager.class);
    private final PresidioInputPersistencyService persistencyService;

    private final AdeDataService adeDataService;
    private final OutputDataServiceSDK outputDataServiceSDK;

    public InputCoreManager(PresidioInputPersistencyService persistencyService, AdeDataService adeDataService,
                            OutputDataServiceSDK outputDataServiceSDK) {
        this.persistencyService = persistencyService;
        this.adeDataService = adeDataService;
        this.outputDataServiceSDK = outputDataServiceSDK;
    }

    public void run(Schema schema, Instant startDate, Instant endDate) {
        int pageSize = 1000;
        RawEventsPageIterator rawEventsPageIterator = new RawEventsPageIterator(startDate, endDate, persistencyService, schema, pageSize);
        TransformationManager transformationManager = SchemaFactory.getTransformationManager(schema);
        InputAdeConverter inputAdeConverter = SchemaFactory.getInputAdeConverter(schema);
        InputOutputConverter inputOutputConverter = SchemaFactory.getInputOutputConverter(schema);

        while (rawEventsPageIterator.hasNext()) {
            List nextEvents = rawEventsPageIterator.next();

            logger.debug("Processing {} events", nextEvents.size());

            List transformedEvents = transformationManager.run(nextEvents);
            storeToAde(schema, startDate, endDate, inputAdeConverter, transformedEvents);
            try {
                storeToOutput(inputOutputConverter, transformedEvents, schema);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void storeToOutput(InputOutputConverter inputOutputConverter, List<? extends AbstractAuditableDocument> transformedEvents, Schema schema) throws Exception {
        List convertedOutputRecords = new ArrayList<>();
        transformedEvents.forEach(doc -> convertedOutputRecords.add(inputOutputConverter.convert(doc)));
        outputDataServiceSDK.store(schema, convertedOutputRecords);
    }

    private void storeToAde(Schema schema, Instant startDate, Instant endDate, InputAdeConverter inputAdeConverter, List<? extends AbstractAuditableDocument> transformedEvents) {
        List convertedAdeRecords = new ArrayList<>();
        transformedEvents.forEach(doc -> convertedAdeRecords.add(inputAdeConverter.convert(doc)));
        adeDataService.store(schema, startDate, endDate, convertedAdeRecords);
    }

}
