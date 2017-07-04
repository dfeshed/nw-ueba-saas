package fortscale.ml.scorer.records;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.AdeRecordReaderFactory;
import presidio.ade.domain.record.scored.AdeScoredRecordReaderFactory;

/**
 * Created by barak_schuster on 6/29/17.
 */
@Configuration
public class AdeRecordReaderFactoriesConfig {

    // TODO: Configure here all relevant record reader factories

    @Bean
    public AdeRecordReaderFactory adeRecordReaderFactory() {
        return new AdeRecordReaderFactory();
    }

    @Bean
    public AdeScoredRecordReaderFactory adeScoredRecordReaderFactory() {
        return new AdeScoredRecordReaderFactory();
    }

}
