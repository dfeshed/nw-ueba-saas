package presidio.input.pre.processing.application;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.input.pre.processing.pre.processor.PreProcessor;

import java.util.Collection;

@Configuration
@Import(LastOccurrenceInstantPreProcessorConfiguration.class)
public class PresidioInputPreProcessingConfiguration {
    private final Collection<PreProcessor<?>> preProcessors;

    @Autowired
    public PresidioInputPreProcessingConfiguration(Collection<PreProcessor<?>> preProcessors) {
        Validate.notEmpty(preProcessors, "preProcessors cannot be empty.");
        Validate.noNullElements(preProcessors, "preProcessors cannot contain null elements.");
        this.preProcessors = preProcessors;
    }

    @Bean
    public PresidioInputPreProcessingService presidioInputPreProcessingService() {
        return new PresidioInputPreProcessingService(preProcessors);
    }
}
