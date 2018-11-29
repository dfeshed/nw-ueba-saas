package presidio.output.domain.spring;

import fortscale.utils.recordreader.transformation.Transformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.RecordReaderFactoryServiceConfig;
import presidio.output.domain.records.EnrichedEventRecordReaderFactory;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Import(RecordReaderFactoryServiceConfig.class)
public class OutputRecordReaderFactoryServiceConfig {
    @Autowired
    private Collection<Transformation<?>> transformations;

    @Bean
    public EnrichedEventRecordReaderFactory enrichedEventRecordReaderFactory() {
        Map<String, Transformation<?>> defaultTransformations = transformations.stream()
                .collect(Collectors.toMap(Transformation::getFeatureName, Function.identity()));
        return new EnrichedEventRecordReaderFactory(defaultTransformations);
    }
}
