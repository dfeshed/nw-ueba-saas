package presidio.input.core.services.impl;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import presidio.input.core.RawEventsPageIterator;
import presidio.input.core.services.converters.ConverterService;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.transformation.managers.TransformationService;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.List;

public class InputCoreManager {

    private static final Logger logger = Logger.getLogger(InputCoreManager.class);

    private final int DEFAULT_PAGE_SIZE = 1000;
    private final int SINGLE_PAGE_SIZE = 1;

    private final PresidioInputPersistencyService persistencyService;
    private final AdeDataService adeDataService;
    private final OutputDataServiceSDK outputDataServiceSDK;
    private final TransformationService transformationService;
    private final ConverterService converterService;

    @Value("${page.iterator.page.size}")
    private Integer pageSize;

    public InputCoreManager(PresidioInputPersistencyService persistencyService, AdeDataService adeDataService,
                            OutputDataServiceSDK outputDataServiceSDK, TransformationService transformationService, ConverterService converterService) {
        this.persistencyService = persistencyService;
        this.adeDataService = adeDataService;
        this.outputDataServiceSDK = outputDataServiceSDK;
        this.transformationService = transformationService;
        this.converterService = converterService;
    }

    public void run(Schema schema, Instant startDate, Instant endDate) {
        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        RawEventsPageIterator rawEventsPageIterator = new RawEventsPageIterator(startDate, endDate, persistencyService, schema, pageSize);

        while (rawEventsPageIterator.hasNext()) {
            try {
                List nextEvents = rawEventsPageIterator.next();

                logger.debug("Processing {} events", nextEvents.size());

                List transformedEvents = transformationService.run(nextEvents, schema);
                storeToAde(schema, startDate, endDate, transformedEvents);
                try {
                    storeToOutput(transformedEvents, schema);
                } catch (Exception e) {
                    logger.error("Error storing transformed data to output ", e);
                }
            } catch (IllegalArgumentException ex) {
                //TODO: there should be beter way to handle this scenario
                logger.error("Error reading events from repository.", ex);
                int num = rawEventsPageIterator.getPageSize();
                rawEventsPageIterator.setPageSize(SINGLE_PAGE_SIZE);
                for (int i = 0; i < num; i++) {
                    try {
                        List nextEvent = rawEventsPageIterator.next();
                        List transformedEvent = transformationService.run(nextEvent, schema);
                        storeToAde(schema, startDate, endDate, transformedEvent);
                        try {
                            storeToOutput(transformedEvent, schema);
                        } catch (Exception e) {
                            logger.error("Error storing transformed data to output ", e);
                        }
                    } catch (IllegalArgumentException e) {
                        logger.error("Bad argument in repository.", ex);
                    }
                }

            }
        }
    }

    private void storeToOutput(List<? extends AbstractInputDocument> transformedEvents, Schema schema) throws Exception {
        outputDataServiceSDK.store(schema, converterService.convertToOutput(transformedEvents, schema));
    }

    private void storeToAde(Schema schema, Instant startDate, Instant endDate, List<? extends AbstractAuditableDocument> transformedEvents) {
        adeDataService.store(schema, startDate, endDate, converterService.convertToAde(transformedEvents, schema));
    }

}