package fortscale.ml.scorer.records;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.AdeAggregationRecordReaderFactory;
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
    public AdeAggregationRecordReaderFactory adeAggregationRecordReaderFactory() {
        return new AdeAggregationRecordReaderFactory();
    }

    @Bean
    public AdeScoredEnrichedRecordReaderFactory adeScoredEnrichedRecordReaderFactory() {
        return new AdeScoredEnrichedRecordReaderFactory();
    }
}
