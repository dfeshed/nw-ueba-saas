package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.ml.scorer.records.AdeRecordReaderFactoriesConfig;
import fortscale.ml.scorer.records.RecordReaderFactoryServiceConfig;
import fortscale.ml.scorer.records.TransformationConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({
//        common application confs
        TransformationConfig.class,
        AdeRecordReaderFactoriesConfig.class})
public class ScoringAggregationsRecordReaderFactoryServiceConfig extends RecordReaderFactoryServiceConfig {
}
