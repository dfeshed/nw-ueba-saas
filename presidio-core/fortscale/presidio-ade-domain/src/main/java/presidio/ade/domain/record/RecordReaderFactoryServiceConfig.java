package presidio.ade.domain.record;

import fortscale.utils.recordreader.RecordReaderFactory;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.recordreader.transformation.Transformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 6/29/17.
 */
@Configuration
@Import({AdeRecordReaderFactoriesConfig.class, TransformationConfig.class})
public class RecordReaderFactoryServiceConfig {
    @Autowired
    private Collection<RecordReaderFactory> recordReaderFactories;
    @Autowired
    private Collection<Transformation<?>> transformations;

    @Bean
    public RecordReaderFactoryService recordReaderFactoryService() {
        return new RecordReaderFactoryService(recordReaderFactories,
                transformations.stream().collect(Collectors.toMap(Transformation::getFeatureName, Function.identity())));
    }

}
