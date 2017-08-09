package presidio.ade.domain.store.accumulator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggrRecordsMetadata;


@Configuration
public class AccumulatedDataToCollectionNameTranslatorConfig {
    @Bean
    public AccumulatedDataToCollectionNameTranslator accumulatedDataToCollectionNameTranslator() {
        return new AccumulatedDataToCollectionNameTranslator();
    }
}
