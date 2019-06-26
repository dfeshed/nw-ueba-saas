package presidio.input.pre.processing.application;

import fortscale.domain.lastoccurrenceinstant.LastOccurrenceInstantStore;
import fortscale.domain.lastoccurrenceinstant.LastOccurrenceInstantStoreConfiguration;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.pre.processing.pre.processor.LastOccurrenceInstantPreProcessor;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
@Import({PresidioInputPersistencyServiceConfig.class, LastOccurrenceInstantStoreConfiguration.class})
public class LastOccurrenceInstantPreProcessorConfiguration {
    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final int rawEventsPageSize;
    private final LastOccurrenceInstantStore lastOccurrenceInstantStore;

    @Autowired
    public LastOccurrenceInstantPreProcessorConfiguration(
            PresidioInputPersistencyService presidioInputPersistencyService,
            @Value("${presidio.input.pre.processing.raw.events.page.size:1000}") int rawEventsPageSize,
            LastOccurrenceInstantStore lastOccurrenceInstantStore) {

        Validate.notNull(presidioInputPersistencyService, "presidioInputPersistencyService cannot be null.");
        Validate.isTrue(rawEventsPageSize > 0, "rawEventsPageSize must be greater than zero.");
        Validate.notNull(lastOccurrenceInstantStore, "lastOccurrenceInstantStore cannot be null.");
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.rawEventsPageSize = rawEventsPageSize;
        this.lastOccurrenceInstantStore = lastOccurrenceInstantStore;
    }

    @Bean
    public LastOccurrenceInstantPreProcessor lastOccurrenceInstantPreProcessor() {
        return new LastOccurrenceInstantPreProcessor(
                "last_occurrence_instant",
                presidioInputPersistencyService,
                rawEventsPageSize,
                lastOccurrenceInstantStore);
    }
}
