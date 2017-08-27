package fortscale.ml.scorer.records;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.AdeAggregationReaderFactory;
import presidio.ade.domain.record.AdeRecordReaderFactory;
import presidio.ade.domain.record.AdeScoredEnrichedRecordReaderFactory;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
@Configuration
public class AdeRecordReaderFactoriesConfig {
    // TODO: Configure here all relevant record reader factories

    @Bean
    public AdeRecordReaderFactory adeRecordReaderFactory() {
        return new AdeRecordReaderFactory();
    }

    @Bean
    public AdeAggregationReaderFactory adeAggregationReaderFactory() {
        return new AdeAggregationReaderFactory();
    }

    @Bean
    public AdeScoredEnrichedRecordReaderFactory adeScoredEnrichedRecordReaderFactory() {
        return new AdeScoredEnrichedRecordReaderFactory();
    }
}
