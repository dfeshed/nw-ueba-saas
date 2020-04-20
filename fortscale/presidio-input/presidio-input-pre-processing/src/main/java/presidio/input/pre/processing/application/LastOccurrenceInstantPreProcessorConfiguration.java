package presidio.input.pre.processing.application;

import fortscale.domain.lastoccurrenceinstant.writer.LastOccurrenceInstantWriter;
import fortscale.domain.lastoccurrenceinstant.writer.LastOccurrenceInstantWriterCacheConfiguration;
import fortscale.utils.mongodb.config.MongoConfig;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.pre.processing.pre.processor.LastOccurrenceInstantPreProcessor;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
@Import({
        MongoConfig.class, // Needed for the Presidio Input Persistency Service.
        PresidioInputPersistencyServiceConfig.class,
        LastOccurrenceInstantWriterCacheConfiguration.class
})
public class LastOccurrenceInstantPreProcessorConfiguration {
    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final int lastOccurrenceInstantWriterMaxSize;
    private final LastOccurrenceInstantWriter lastOccurrenceInstantWriter;

    @Autowired
    public LastOccurrenceInstantPreProcessorConfiguration(
            PresidioInputPersistencyService presidioInputPersistencyService,
            @Value("${presidio.last.occurrence.instant.writer.maximum.size}") int lastOccurrenceInstantWriterMaxSize,
            @Qualifier("lastOccurrenceInstantWriterCache") LastOccurrenceInstantWriter lastOccurrenceInstantWriter) {

        Validate.notNull(presidioInputPersistencyService, "presidioInputPersistencyService cannot be null.");
        Validate.isTrue(lastOccurrenceInstantWriterMaxSize > 0, "lastOccurrenceInstantWriterMaxSize must be greater than zero.");
        Validate.notNull(lastOccurrenceInstantWriter, "lastOccurrenceInstantWriter cannot be null.");
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.lastOccurrenceInstantWriterMaxSize = lastOccurrenceInstantWriterMaxSize;
        this.lastOccurrenceInstantWriter = lastOccurrenceInstantWriter;
    }

    @Bean
    public LastOccurrenceInstantPreProcessor lastOccurrenceInstantPreProcessor() {
        return new LastOccurrenceInstantPreProcessor(
                "last_occurrence_instant",
                presidioInputPersistencyService,
                lastOccurrenceInstantWriterMaxSize,
                lastOccurrenceInstantWriter);
    }
}
